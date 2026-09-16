package com.library.library_management_api.integration;

import com.library.library_management_api.model.Book;
import com.library.library_management_api.model.BookRecord;
import com.library.library_management_api.model.Member;
import com.library.library_management_api.repository.BookRecordRepository;
import com.library.library_management_api.repository.BookRepository;
import com.library.library_management_api.repository.BorrowRecordRepository;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class BorrowApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BookRecordRepository bookRecordRepository;

    @Autowired
    private BorrowRecordRepository borrowRecordRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Member member;
    private BookRecord bookRecord;

    @BeforeEach
    void setUp(){
        member = memberRepository.save(new Member("John","john@gmail.com","0000000000"));
        Book book = bookRepository.save(new Book("Test Title", "Test Author", "Test Publisher", 2010, 2,"1FB3KRB3BKETBKB"));
        bookRecord=bookRecordRepository.save(new BookRecord(book, LocalDate.of(2026,8,7)));

        UserAccount userAccount = new UserAccount(
                "employee@test.com",
                passwordEncoder.encode("password123"),
                Role.EMPLOYEE,
                null);
        userAccountRepository.save(userAccount);
    }

    private String loginAndGetToken(String email, String password) throws Exception{
        String loginBody = """
                {
                    "email":"%s",
                    "password":"%s"
                }
                """.formatted(email,password);
        MvcResult loginResult = mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody)
        )
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = loginResult.getResponse().getContentAsString();
        LoginResponse loginResponse =objectMapper.readValue(responseBody, LoginResponse.class);
        return loginResponse.token();
    }

    @Test
    void issueBook_shouldCreateBorrowRecord() throws Exception {
        String token = loginAndGetToken("employee@test.com","password123");
        String requestBody = """
                {
                    "memberId": %d,
                    "bookRecordId": %d
                }
                """.formatted(member.getMemberId(),bookRecord.getBookRecordId());
        mockMvc.perform(
                post("/borrow-records")
                        .header(HttpHeaders.AUTHORIZATION,"Bearer "+token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void issueBook_shouldReturnConflict_whenBookIsAlreadyIssued() throws Exception {
        String token = loginAndGetToken("employee@test.com","password123");
        String requestBody = """
                {
                    "memberId": %d,
                    "bookRecordId": %d
                }
                """.formatted(member.getMemberId(), bookRecord.getBookRecordId());
        mockMvc.perform(
                post("/borrow-records")
                        .header("Authorization","Bearer "+token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andDo(print())
                .andExpect(status().isCreated());
        mockMvc.perform(
                post("/borrow-records")
                        .header("Authorization","Bearer "+token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("Book Record not available"))
                .andExpect(jsonPath("$.path").value("/borrow-records"));
    }
}
