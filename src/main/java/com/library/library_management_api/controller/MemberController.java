package com.library.library_management_api.controller;

import com.library.library_management_api.dto.CreateMemberRequest;
import com.library.library_management_api.dto.MemberResponse;
import com.library.library_management_api.dto.PayFineRequest;
import com.library.library_management_api.dto.UpdateMemberRequest;
import com.library.library_management_api.model.Member;
import com.library.library_management_api.security.service.UserAccountService;
import com.library.library_management_api.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/members")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Members", description = "Operations for managing members, viewing member details and paying fines")
@ApiResponses({
        @ApiResponse(responseCode = "401",description = "Authentication required or JWT is invalid")
})
public class MemberController {

    private final MemberService memberService;
    private final UserAccountService userAccountService;

    public MemberController(MemberService memberService, UserAccountService userAccountService){
        this.memberService=memberService;
        this.userAccountService=userAccountService;
    }



    @Operation(summary = "Creating a library member",
            description = "Creates a new library member. Requires EMPLOYEE or ADMIN role")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Member created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid member creation request data"),
            @ApiResponse(responseCode = "403", description = "User does not have the required role"),
            @ApiResponse(responseCode = "409", description = "Member with the provided email already exists")
    })
    @PostMapping
    public ResponseEntity<MemberResponse> createMember(@Valid @RequestBody CreateMemberRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(memberService.createMember(request));
    }


    @Operation(summary = "Get all library members",
            description = "Retrieves all library member records. Requires EMPLOYEE or ADMIN role")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "Members retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "User does not have the required role")
    })
    @GetMapping
    public ResponseEntity<List<MemberResponse>> getAllMembers(){
        return ResponseEntity.status(HttpStatus.OK).body(memberService.getAllMembers());
    }



    @Operation(summary = "Get a specific member details by entering the member id",
            description = "Retrieves a library member's details by member ID. Requires EMPLOYEE or ADMIN role")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "Member details retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "User does not have the required role"),
            @ApiResponse(responseCode = "404", description = "Member not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MemberResponse> getMemberById(@PathVariable("id") Long memberId){
        return ResponseEntity.ok(memberService.getMemberResponseById(memberId));
    }



    @Operation(summary = "Update member details",
            description = "Updates a member's name, email and phone. Requires EMPLOYEE or ADMIN role")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Member's details updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid member details"),
            @ApiResponse(responseCode = "403", description = "User does not have the required role"),
            @ApiResponse(responseCode = "404", description = "Member with the specified ID not found"),
            @ApiResponse(responseCode = "409", description = "Another member exists with the provided email")
    })
    @PutMapping("/{id}")
    public ResponseEntity<MemberResponse> updateMember(@PathVariable("id") Long memberId,
                                               @Valid @RequestBody UpdateMemberRequest request){
        return ResponseEntity.status(HttpStatus.OK).body((memberService.updateMember(memberId,request)));
    }


    @Operation(summary = "Deactivate a library member",
            description = "Deactivates a member who has not active borrow records or outstanding fines. Requires EMPLOYEE or ADMIN role")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Member deactivated successfully"),
            @ApiResponse(responseCode = "403", description = "User does not have the required role"),
            @ApiResponse(responseCode = "404", description = "Member with the specified ID not found"),
            @ApiResponse(responseCode = "409", description = "Member has active borrow records or outstanding fines")
    })
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<MemberResponse> deactivateMember(@PathVariable("id") Long memberId){
        return ResponseEntity.ok(memberService.deactivateMember(memberId));
    }



    @Operation(summary = "Activate a member",
            description = "Activates a library member account. Requires EMPLOYEE or ADMIN role")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Member activated successfully"),
            @ApiResponse(responseCode = "403", description = "User does not have the required role"),
            @ApiResponse(responseCode = "404", description = "Member with the specified ID not found")
    })
    @PatchMapping("/{id}/activate")
    public ResponseEntity<MemberResponse> activateMember(@PathVariable("id") Long memberId){
        return ResponseEntity.ok(memberService.activateMember(memberId));
    }



    @Operation(summary = "Pay a member's outstanding fine",
            description = "Records a fine payment for the specified member. Requires EMPLOYEE or ADMIN role")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Fine payment successful"),
            @ApiResponse(responseCode = "400", description = "Payment amount must be greater than zero and cannot exceed the outstanding fine"),
            @ApiResponse(responseCode = "403", description = "User does not have the required role"),
            @ApiResponse(responseCode = "404", description = "Member with the specified ID not found")
    })
    @PostMapping("/{id}/pay-fine")
    public ResponseEntity<MemberResponse> payFine(@PathVariable("id") Long memberId,
                                          @Valid @RequestBody PayFineRequest request){
        MemberResponse updatedMemberResponse =memberService.payFine(memberId,request);
        return ResponseEntity.ok(updatedMemberResponse);
    }



    @Operation(summary = "Get my member details",
            description = "Retrieves the authenticated member's details. Requires MEMBER role")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Member details retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied. Only users with MEMBER role can access this end point"),
            @ApiResponse(responseCode = "404", description = "Member with the specified ID not found"),
            @ApiResponse(responseCode = "409", description = "Logged-in user has no associated member record")
    })
    @GetMapping("/me")
    public ResponseEntity<MemberResponse> getMyMemberDetails(Authentication authentication){
        String email = authentication.getName();
        Long memberId = userAccountService.getMemberIdForUser(email);
        return ResponseEntity.ok(memberService.getMemberResponseById(memberId));
    }



    @Operation(summary = "Pay my outstanding fine",
            description = "Records the fine payment for the currently authenticated in member. Requires MEMBER role")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Fine payment successful"),
            @ApiResponse(responseCode = "400", description = "Payment amount must be greater than zero and cannot exceed the outstanding fine"),
            @ApiResponse(responseCode = "403", description = "Access denied. Only users with MEMBER role can access this end point"),
            @ApiResponse(responseCode = "404", description = "Member with the specified ID not found"),
            @ApiResponse(responseCode = "409", description = "Logged-in user has no associated member record")
    })
    @PostMapping("/me/pay-fine")
    public ResponseEntity<MemberResponse> payMyFine(@Valid @RequestBody PayFineRequest request,
                                            Authentication authentication){
        String email = authentication.getName();
        Long memberId = userAccountService.getMemberIdForUser(email);
        return ResponseEntity.ok(memberService.payFine(memberId,request));
    }
}
