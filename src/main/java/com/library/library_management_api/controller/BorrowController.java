package com.library.library_management_api.controller;

import com.library.library_management_api.dto.*;
import com.library.library_management_api.security.service.UserAccountService;
import com.library.library_management_api.service.BorrowService;
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
@RequestMapping("/borrow-records")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Borrow records", description = "Operations for issuing,returning,viewing and managing book borrow records")
@ApiResponse(responseCode = "401", description = "Authentication required or JWT is invalid")
public class BorrowController {

    private final BorrowService borrowService;
    private final UserAccountService userAccountService;

    public BorrowController(BorrowService borrowService,UserAccountService userAccountService){
        this.borrowService=borrowService;
        this.userAccountService=userAccountService;
    }


    @PostMapping
    @Operation(summary = "Issue a book copy to a member",
                description = "Issues an available book copy to an active eligible member. Requires EMPLOYEE or ADMIN role")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Book issued successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid issue-book request data"),
            @ApiResponse(responseCode = "403", description = "User does not have the required role"),
            @ApiResponse(responseCode = "404", description = "Book copy or member not found"),
            @ApiResponse(responseCode = "409",
                    description = "Book cannot be issued because of its availability or the member's current borrowing status")
    })
    public ResponseEntity<BorrowRecordResponse> issueBook(@Valid @RequestBody IssueBookRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(borrowService.issueBook(request));
    }




    @GetMapping
    @Operation(summary = "Get all borrow records",
            description = "Retrieves all the borrowing records in the library. Requires EMPLOYEE or ADMIN role")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Borrow records retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "User does not have the required role")
    })
    public ResponseEntity<List<BorrowRecordResponse>> getAllBorrowRecords(){
        return ResponseEntity.ok(borrowService.getAllBorrowRecords());
    }




    @GetMapping("/{id}")
    @Operation(summary = "Get a specific borrow record by entering the borrow id",
                description = "Retrieves a specific borrow record. Requires EMPLOYEE or ADMIN role")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Borrow record retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "User does not have the required role"),
            @ApiResponse(responseCode = "404", description = "Borrow record not found")
    })
    public ResponseEntity<BorrowRecordResponse> getBorrowRecordById(@PathVariable("id") Long borrowId){
        return ResponseEntity.ok(borrowService.getBorrowRecordResponseById(borrowId));
    }





    @GetMapping("/member/{id}")
    @Operation(summary = "Get the borrow history of a member",
            description = "Retrieves all the borrowing records of a specific member. Requires EMPLOYEE or ADMIN role")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Borrow history of the member retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "User does not have the required role"),
            @ApiResponse(responseCode = "404", description = "Member not found")
    })
    public ResponseEntity<List<BorrowRecordResponse>> getBorrowHistoryByMemberId(@PathVariable("id") Long memberId){
        return ResponseEntity.ok(borrowService.getBorrowHistoryByMemberId(memberId));
    }





    @GetMapping("/member/{id}/active")
    @Operation(summary = "Get all the active borrow records of a member",
            description = "Retrieves all the active borrowing records of a specific member. Requires EMPLOYEE or ADMIN role")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Active borrow records of the member retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "User does not have the required role"),
            @ApiResponse(responseCode = "404", description = "Member not found")
    })
    public ResponseEntity<List<BorrowRecordResponse>> getActiveBorrowsByMemberId(@PathVariable("id") Long memberId){
        return ResponseEntity.ok(borrowService.getActiveBorrowsByMemberId(memberId));
    }





    @PostMapping("/{id}/return")
    @Operation(summary = "Returns a borrowed book copy",
            description = "Marks the borrow record as returned and makes the book copy as available, applies any overdue fine if applicable." +
                    "Voided or already returned borrow record cannot be returned. Requires EMPLOYEE or ADMIN role")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book returned successfully"),
            @ApiResponse(responseCode = "403", description = "User does not have the required role"),
            @ApiResponse(responseCode = "404", description = "Borrow record not found"),
            @ApiResponse(responseCode = "409", description = "Book copy cannot be returned due to its current status")
    })
    public ResponseEntity<BookReturnResponse> returnBook(@PathVariable("id") Long borrowId){
        return ResponseEntity.ok(borrowService.returnBook(borrowId));
    }





    @GetMapping("/overdue")
    @Operation(summary = "Get all the overdue borrow records",
            description = "Retrieves active borrowing records whose due dates have passed. Requires EMPLOYEE or ADMIN role")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Overdue borrow records retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "User does not have the required role")
    })
    public ResponseEntity<List<OverdueBorrowResponse>> getOverdueBorrowRecords(){
        return ResponseEntity.ok(borrowService.getOverdueBorrowRecords());
    }





    @PatchMapping("/{id}/void")
    @Operation(summary = "Voids a borrow record",
            description = "Voids a borrowing transaction, records who performed the action and why, makes the book copy available" +
                    "Only ISSUED borrow records can be voided. Requires EMPLOYEE or ADMIN role")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Borrow record voided successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid void request data"),
            @ApiResponse(responseCode = "403", description = "User does not have the required role"),
            @ApiResponse(responseCode = "404", description = "Borrow record not found"),
            @ApiResponse(responseCode = "409", description = "Borrow record cannot be voided because of its current state")
    })
    public ResponseEntity<Void> voidBorrowRecord(@PathVariable("id") Long borrowId,
                                                 @Valid @RequestBody VoidBorrowRecordRequest request,
                                                 Authentication authentication){
        String voidedBy = authentication.getName();
        borrowService.voidBorrowRecord(borrowId,voidedBy,request.reason());
        return ResponseEntity.noContent().build();
    }





    @GetMapping("/me")
    @Operation(summary = "Get my borrowing history",
            description = "Retrieves the currently authenticated member's borrowing history. Requires MEMBER role")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Borrow records retrieved successfully"),
            @ApiResponse(responseCode = "409", description = "Current user is not a member")
    })
    public ResponseEntity<List<BorrowRecordResponse>> getMyBorrowRecords(Authentication authentication){
        String email = authentication.getName();
        Long memberId = userAccountService.getMemberIdForUser(email);
        return ResponseEntity.ok(borrowService.getBorrowHistoryByMemberId(memberId));
    }





    @GetMapping("/me/active")
    @Operation(summary = "Get my active borrow records",
            description = "Retrieves the authenticated member's currently active borrow records. Requires MEMBER role")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Active borrow records retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied. Only users with MEMBER role can access this end point"),
            @ApiResponse(responseCode = "409", description = "Logged-in user has no associated member record")
    })
    public ResponseEntity<List<BorrowRecordResponse>> getMyActiveBorrowRecords(Authentication authentication){
        String email = authentication.getName();
        Long memberId = userAccountService.getMemberIdForUser(email);
        return ResponseEntity.ok(borrowService.getActiveBorrowsByMemberId(memberId));
    }
}
