package com.library.library_management_api.service;

import com.library.library_management_api.dto.CreateMemberRequest;
import com.library.library_management_api.dto.MemberResponse;
import com.library.library_management_api.dto.PayFineRequest;
import com.library.library_management_api.dto.UpdateMemberRequest;
import com.library.library_management_api.exception.DuplicateMemberException;
import com.library.library_management_api.exception.MemberNotFoundException;
import com.library.library_management_api.model.BorrowStatus;
import com.library.library_management_api.model.Member;
import com.library.library_management_api.model.MemberStatus;
import com.library.library_management_api.repository.BorrowRecordRepository;
import com.library.library_management_api.repository.MemberRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final BorrowRecordRepository borrowRecordRepository;

    public MemberService(MemberRepository memberRepository,BorrowRecordRepository borrowRecordRepository){
        this.memberRepository=memberRepository;
        this.borrowRecordRepository=borrowRecordRepository;
    }

    public MemberResponse createMember(CreateMemberRequest request){
        boolean duplicateExists = memberRepository.existsByEmailIgnoreCase(request.getEmail());
        if(duplicateExists){
            throw new DuplicateMemberException("Member with this email already exists");
        }
        Member member = new Member(request.getName(),request.getEmail(),request.getPhone());
        Member savedMember = memberRepository.save(member);
        return toResponse(savedMember);
    }

    private MemberResponse toResponse(Member member){
        return new MemberResponse(
                member.getMemberId(),
                member.getName(),
                member.getEmail(),
                member.getPhone(),
                member.getOutstandingFine(),
                member.getStatus()
        );
    }

    public List<MemberResponse> getAllMembers(){
        return memberRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public Member getMemberById(Long memberId){
        return memberRepository.findById(memberId)
                .orElseThrow(()->new MemberNotFoundException("Member not found with ID: "+memberId));
    }

    public MemberResponse getMemberResponseById(Long memberId){
        return toResponse(getMemberById(memberId));
    }

    public MemberResponse updateMember(Long memberId, UpdateMemberRequest request){
        Member existingMember = getMemberById(memberId);
        if(!existingMember.getEmail().equalsIgnoreCase(request.getEmail())
            && memberRepository.existsByEmailIgnoreCase(request.getEmail())){
            throw new DuplicateMemberException("Member with this email already exists");
        }

        existingMember.updateDetails(request.getName(),request.getEmail(), request.getPhone());
        Member updatedMember = memberRepository.save(existingMember);
        return toResponse(updatedMember);
    }

    @Transactional
    public MemberResponse deactivateMember(Long memberId){
        Member member = getMemberById(memberId);
        long activeBorrowCount = borrowRecordRepository
                .countByMemberMemberIdAndStatus(memberId, BorrowStatus.ACTIVE);
        if(activeBorrowCount>0)
            throw new IllegalStateException("Member cannot be deactivated while active borrows exist");
        if(member.getOutstandingFine()>0)
            throw new IllegalStateException("Member cannot be deactivated while outstanding fine exist");
        member.deactivate();
        return toResponse(member);
    }

    @Transactional
    public MemberResponse activateMember(Long memberId){
        Member member = getMemberById(memberId);
        member.activate();
        return toResponse(member);
    }


    @Transactional // no need of memberRepo.save()
    public MemberResponse payFine(Long memberId, PayFineRequest request){
        Member member=getMemberById(memberId);
        member.recordFinePayment(request.getAmount());
        return toResponse(member);
    }
}
