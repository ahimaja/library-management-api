package com.library.library_management_api.controller;

import com.library.library_management_api.dto.*;
import com.library.library_management_api.model.BorrowRecord;
import com.library.library_management_api.security.service.UserAccountService;
import com.library.library_management_api.service.BorrowService;
import jakarta.validation.Valid;
import org.hibernate.sql.ast.tree.expression.Over;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/borrow-records")
public class BorrowController {

    private final BorrowService borrowService;
    private final UserAccountService userAccountService;

    public BorrowController(BorrowService borrowService,UserAccountService userAccountService){
        this.borrowService=borrowService;
        this.userAccountService=userAccountService;
    }

    @PostMapping
    public ResponseEntity<BorrowRecordResponse> issueBook(@Valid @RequestBody IssueBookRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(borrowService.issueBook(request));
    }

    @GetMapping
    public ResponseEntity<List<BorrowRecordResponse>> getAllBorrowRecords(){
        return ResponseEntity.ok(borrowService.getAllBorrowRecords());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BorrowRecordResponse> getBorrowRecordById(@PathVariable("id") Long borrowId){
        return ResponseEntity.ok(borrowService.getBorrowRecordResponseById(borrowId));
    }

    @GetMapping("/member/{id}")
    public ResponseEntity<List<BorrowRecordResponse>> getBorrowHistoryByMemberId(@PathVariable("id") Long memberId){
        return ResponseEntity.ok(borrowService.getBorrowHistoryByMemberId(memberId));
    }

    @GetMapping("/member/{id}/active")
    public ResponseEntity<List<BorrowRecordResponse>> getActiveBorrowsByMemberId(@PathVariable("id") Long memberId){
        return ResponseEntity.ok(borrowService.getActiveBorrowsByMemberId(memberId));
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<BookReturnResponse> returnBook(@PathVariable("id") Long borrowId){
        return ResponseEntity.ok(borrowService.returnBook(borrowId));
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<OverdueBorrowResponse>> getOverdueBorrowRecords(){
        return ResponseEntity.ok(borrowService.getOverdueBorrowRecords());
    }

    @PatchMapping("/{id}/void")
    public ResponseEntity<Void> voidBorrowRecord(@PathVariable("id") Long borrowId,
                                                 @Valid @RequestBody VoidBorrowRecordRequest request,
                                                 Authentication authentication){
        String voidedBy = authentication.getName();
        borrowService.voidBorrowRecord(borrowId,voidedBy,request.reason());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<List<BorrowRecordResponse>> getMyBorrowRecords(Authentication authentication){
        String email = authentication.getName();
        Long memberId = userAccountService.getMemberIdForUser(email);
        return ResponseEntity.ok(borrowService.getBorrowHistoryByMemberId(memberId));
    }

    @GetMapping("/me/active")
    public ResponseEntity<List<BorrowRecordResponse>> getMyActiveBorrowRecords(Authentication authentication){
        String email = authentication.getName();
        Long memberId = userAccountService.getMemberIdForUser(email);
        return ResponseEntity.ok(borrowService.getActiveBorrowsByMemberId(memberId));
    }
}
