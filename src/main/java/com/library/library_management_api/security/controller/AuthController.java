package com.library.library_management_api.security.controller;

import com.library.library_management_api.security.dto.*;
import com.library.library_management_api.security.service.UserAccountService;
import com.library.library_management_api.security.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Authentication", description = "Operations for member registration and user login")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserAccountService userAccountService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(
            UserAccountService userAccountService,
            AuthenticationManager authenticationManager,
            JwtService jwtService){
        this.userAccountService=userAccountService;
        this.authenticationManager=authenticationManager;
        this.jwtService=jwtService;
    }




    @Operation(summary = "Register a member account",
            description = "Creates a login account with MEMBER role for an existing library member. No authentication required")
    @ApiResponses({
            @ApiResponse(responseCode = "201",description = "Member registered successfully"),
            @ApiResponse(responseCode = "400",description = "Invalid member registration data"),
            @ApiResponse(responseCode = "404", description = "Member with specified member ID not found"),
            @ApiResponse(responseCode = "409", description = "Email is already registered for another user or the member already has a user account")
    })
    @PostMapping("/register")
    public ResponseEntity<UserAccountResponse> registerMember(@Valid @RequestBody RegisterMemberRequest memberRequest){
        UserAccountResponse response = userAccountService.registerMember(memberRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }




    @Operation(summary = "Log in to the library system",
            description = "Authenticates a user using their email and password and returns a JWT. No authentication required")
    @ApiResponses({
            @ApiResponse(responseCode = "201",description = "Login successful. JWT returned"),
            @ApiResponse(responseCode = "400",description = "Invalid login request data"),
            @ApiResponse(responseCode = "401", description = "Invalid email or password"),
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request){
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                request.getEmail(),request.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails);
        return ResponseEntity.ok(new LoginResponse(token));
    }

}
