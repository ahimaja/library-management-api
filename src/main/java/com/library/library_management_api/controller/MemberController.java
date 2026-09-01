package com.library.library_management_api.controller;

import com.library.library_management_api.dto.CreateMemberRequest;
import com.library.library_management_api.dto.MemberResponse;
import com.library.library_management_api.dto.PayFineRequest;
import com.library.library_management_api.dto.UpdateMemberRequest;
import com.library.library_management_api.model.Member;
import com.library.library_management_api.security.service.UserAccountService;
import com.library.library_management_api.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;
    private final UserAccountService userAccountService;

    public MemberController(MemberService memberService, UserAccountService userAccountService){
        this.memberService=memberService;
        this.userAccountService=userAccountService;
    }

    @PostMapping
    public ResponseEntity<MemberResponse> createMember(@Valid @RequestBody CreateMemberRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(memberService.createMember(request));
    }

    @GetMapping
    public ResponseEntity<List<MemberResponse>> getAllMembers(){
        return ResponseEntity.status(HttpStatus.OK).body(memberService.getAllMembers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberResponse> getMemberById(@PathVariable("id") Long memberId){
        return ResponseEntity.ok(memberService.getMemberResponseById(memberId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MemberResponse> updateMember(@PathVariable("id") Long memberId,
                                               @Valid @RequestBody UpdateMemberRequest request){
        return ResponseEntity.status(HttpStatus.OK).body((memberService.updateMember(memberId,request)));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<MemberResponse> deactivateMember(@PathVariable("id") Long memberId){
        return ResponseEntity.ok(memberService.deactivateMember(memberId));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<MemberResponse> activateMember(@PathVariable("id") Long memberId){
        return ResponseEntity.ok(memberService.activateMember(memberId));
    }


    @PostMapping("/{id}/pay-fine")
    public ResponseEntity<MemberResponse> payFine(@PathVariable("id") Long memberId,
                                          @Valid @RequestBody PayFineRequest request){
        MemberResponse updatedMemberResponse =memberService.payFine(memberId,request);
        return ResponseEntity.ok(updatedMemberResponse);
    }

    @GetMapping("/me")
    public ResponseEntity<MemberResponse> getMyMemberDetails(Authentication authentication){
        String email = authentication.getName();
        Long memberId = userAccountService.getMemberIdForUser(email);
        return ResponseEntity.ok(memberService.getMemberResponseById(memberId));
    }

    @PostMapping("/me/pay-fine")
    public ResponseEntity<MemberResponse> payMyFine(@Valid @RequestBody PayFineRequest request,
                                            Authentication authentication){
        String email = authentication.getName();
        Long memberId = userAccountService.getMemberIdForUser(email);
        return ResponseEntity.ok(memberService.payFine(memberId,request));
    }
}
