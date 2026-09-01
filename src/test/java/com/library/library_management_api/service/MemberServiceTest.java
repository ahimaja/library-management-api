package com.library.library_management_api.service;

import com.library.library_management_api.dto.CreateMemberRequest;
import com.library.library_management_api.dto.MemberResponse;
import com.library.library_management_api.dto.PayFineRequest;
import com.library.library_management_api.exception.DuplicateMemberException;
import com.library.library_management_api.exception.MemberNotFoundException;
import com.library.library_management_api.model.Member;
import com.library.library_management_api.repository.BorrowRecordRepository;
import com.library.library_management_api.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BorrowRecordRepository borrowRecordRepository;

    private MemberService memberService;

    @BeforeEach
    void setUp(){
        memberService = new MemberService(memberRepository,borrowRecordRepository);
    }

    @Test
    void createMember_shouldFail_whenEmailAlreadyExists(){
        when(memberRepository.existsByEmailIgnoreCase("dude_always_exist@funmail.com"))
                .thenReturn(true);
        CreateMemberRequest request = new CreateMemberRequest();
        request.setName("Existing Dude");
        request.setEmail("dude_always_exist@funmail.com");
        request.setPhone("1001111001");
        DuplicateMemberException exception = assertThrows(DuplicateMemberException.class,
                ()->memberService.createMember(request));
        assertEquals("Member with this email already exists",exception.getMessage());
        verify(memberRepository,never()).save(any(Member.class));
    }
    @Test
    void getMemberById_shouldReturnMember_whenMemberExists(){
        Member member = new Member("Hima","hima@gmail.com","9191919191");
        when(memberRepository.findById(1L))
                .thenReturn(Optional.of(member));
        Member returnedMember = memberService.getMemberById(1L);
        assertSame(member,returnedMember);
    }

    @Test
    void getMemberById_shouldFail_whenMemberDoesNotExist(){
        Member member = new Member("Hima","hima@gmail.com","9191919191");
        when(memberRepository.findById(1L))
                .thenReturn(Optional.empty());
        MemberNotFoundException exception = assertThrows(MemberNotFoundException.class,
                ()->memberService.getMemberById(1L));
        assertEquals("Member not found with ID: 1",exception.getMessage());
    }

    @Test
    void payFine_shouldReduceOutstandingFine(){
        Member member = new Member("Hima","hima@gmail.com","9191919191");
        member.addFine(20L);
        when(memberRepository.findById(1L))
                .thenReturn(Optional.of(member));
        PayFineRequest payFineRequest = new PayFineRequest();
        payFineRequest.setAmount(10L);
        MemberResponse returnedMemberResponse = memberService.payFine(1L,payFineRequest);
        assertEquals(10L,returnedMemberResponse.outstandingFine());
    }
}
