package com.library.library_management_api.security;

import com.library.library_management_api.model.Member;
import com.library.library_management_api.repository.MemberRepository;
import com.library.library_management_api.security.dto.LoginResponse;
import com.library.library_management_api.security.model.Role;
import com.library.library_management_api.security.model.UserAccount;
import com.library.library_management_api.security.repository.UserAccountRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class SecurityIntegrationTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @BeforeEach
    void setUp(){
        Member member = new Member("Test Member","member@test.com","0000000000");
        memberRepository.save(member);
        UserAccount userAccount = new UserAccount(
                "member@test.com",
                passwordEncoder.encode("password123"),
                Role.MEMBER,
                member);
        userAccountRepository.save(userAccount);
    }

    private String loginAndGetToken(String email, String password) throws Exception {
        MvcResult loginResult = mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                    "email":"%s",
                                    "password":"%s"
                                }
                                """.formatted(email,password))
                )
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = loginResult.getResponse().getContentAsString();
        LoginResponse loginResponse = objectMapper.readValue(responseBody, LoginResponse.class);
        String token = loginResponse.token();
        //String token = objectMapper.readTree(responseBody).get("token").asString(); without using LoginResponse dto
        return token;
    }

    @Test
    void protectedEndPoint_shouldReturn401_whenJwtIsMissing() throws Exception{
        mockMvc.perform(
                get("/members/me")
        )
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Authentication is required to access this resource"))
                .andExpect(jsonPath("$.path").value("/members/me"));

    }

    @Test
    void protectedEndPoint_shouldReturn200_whenValidMemberJwtIsProvided() throws Exception{
        String token = loginAndGetToken("member@test.com","password123");
        mockMvc.perform(
                get("/members/me")
                        .header("Authorization","Bearer "+token)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("member@test.com"));
    }

    @Test
    void adminEndPoint_shouldReturn403_whenMemberJwtIsProvided() throws Exception{
        String token = loginAndGetToken("member@test.com","password123");
        mockMvc.perform(
                get("/admin/staff")
                        .header("Authorization","Bearer "+token)
        )
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message").value("You do not have permission to access this resource"))
                .andExpect(jsonPath("$.path").value("/admin/staff"));
    }

    @Test
    void protectedEndPoint_shouldReturn401_whenJwtIsTampered() throws Exception {
        String token = loginAndGetToken("member@test.com","password123");
        String[] parts = token.split("\\.");
        String payload = parts[1];
        char replacementCharacter = payload.charAt(0)=='A'?'B':'A';
        String tamperedPayload = replacementCharacter+payload.substring(1);
        String tamperedToken = parts[0]+"."+tamperedPayload+"."+parts[2];
        mockMvc.perform(
                get("/members/me")
                        .header("Authorization","Bearer "+ tamperedToken)
        )
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    void protectedEndPoint_shouldReturn401_whenUserIsDisabledAfterJwtWasIssued() throws Exception{
        String token = loginAndGetToken("member@test.com","password123");
        UserAccount userAccount = userAccountRepository.findByEmailIgnoreCase("member@test.com")
                .orElseThrow(()->new UsernameNotFoundException("User not found"));
        userAccount.disable();
        userAccountRepository.flush(); // can use save() too
        mockMvc.perform(
                get("/members/me")
                        .header("Authorization","Bearer "+token)
        )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    void adminEndPoint_shouldReturn403_whenRoleIsChangedToEmployeeAfterJwtWasIssued() throws Exception {
        UserAccount userAccount = userAccountRepository.findByEmailIgnoreCase("member@test.com")
                .orElseThrow(()->new UsernameNotFoundException("User not found"));
        userAccount.changeRole(Role.ADMIN);
        userAccountRepository.flush();
        String token = loginAndGetToken("member@test.com","password123");
        userAccount.changeRole(Role.EMPLOYEE);
        userAccountRepository.flush();
        mockMvc.perform(
                get("/admin/staff")
                        .header("Authorization","Bearer "+token)
        )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Forbidden"));
    }

}
