package com.library.library_management_api.service;

import com.library.library_management_api.dto.BookReturnResponse;
import com.library.library_management_api.dto.BorrowRecordResponse;
import com.library.library_management_api.dto.IssueBookRequest;
import com.library.library_management_api.dto.OverdueBorrowResponse;
import com.library.library_management_api.exception.*;
import com.library.library_management_api.model.*;
import com.library.library_management_api.repository.BorrowRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BorrowServiceTest {
    @Mock
    private BorrowRecordRepository borrowRecordRepository;

    @Mock
    private BookRecordService bookRecordService;

    @Mock
    private MemberService memberService;

    private BorrowService borrowService;

    private final LocalDate TEST_DATE = LocalDate.of(2026,8,7);

    private final String TEST_VOIDED_BY = "employee@test.com";
    private final String TEST_VOID_REASON = "Test correction";

    @BeforeEach
    void setUp(){
        Clock fixedClock = Clock.fixed(
                TEST_DATE
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant(),
                ZoneId.systemDefault());
        borrowService = new BorrowService(borrowRecordRepository,memberService,bookRecordService,fixedClock);
    }

    @Test
    void issueBook_shouldCreateActiveBorrowRecords(){
        Member member = mock(Member.class);
        BookRecord bookRecord = mock(BookRecord.class);
        Book book = mock(Book.class);

        //setting mock values for issueBook() method
        when(memberService.getMemberById(1L))
                .thenReturn(member);
        when(bookRecordService.getBookRecordById(5L))
                .thenReturn(bookRecord);
        when(bookRecord.isAvailable())
                .thenReturn(true);
        when(member.getOutstandingFine())
                .thenReturn(0L);
        when(borrowRecordRepository.countByMemberMemberIdAndStatus(1L, BorrowStatus.ACTIVE))
                .thenReturn(0L);
        when(borrowRecordRepository.save(any(BorrowRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        //setting mock values for toResponse() method called inside issueBook()
        when(member.getMemberId())
                .thenReturn(1L);
        when(member.getName())
                .thenReturn("Anu");
        when(bookRecord.getBookRecordId())
                .thenReturn(5L);
        when(bookRecord.getBook())
                .thenReturn(book);
        when(book.getTitle())
                .thenReturn("Programming with C");

        IssueBookRequest request = new IssueBookRequest();
        request.setBookRecordId(5L);
        request.setMemberId(1L);

        BorrowRecordResponse response = borrowService.issueBook(request);

        assertEquals(1L,response.getMemberId());
        assertEquals(5L,response.getBookRecordId());
        assertEquals(TEST_DATE,response.getBorrowDate());
        assertEquals(TEST_DATE.plusDays(14),response.getDueDate());
        assertEquals(BorrowStatus.ACTIVE,response.getStatus());
        assertNull(response.getVoidedBy());
        verify(bookRecord).markAsIssued();
        verify(borrowRecordRepository).save(any(BorrowRecord.class));
    }

    @Test
    void issueBook_shouldFail_whenBookRecordIsNotAvailable(){
        Member member = mock(Member.class);
        BookRecord bookRecord = mock(BookRecord.class);
        when(memberService.getMemberById(1L))
                .thenReturn(member);
        when(bookRecordService.getBookRecordById(5L))
                .thenReturn(bookRecord);
        when(bookRecord.isAvailable())
                .thenReturn(false);
        IssueBookRequest request = new IssueBookRequest();
        request.setMemberId(1L);
        request.setBookRecordId(5L);
        assertThrows(BookRecordNotAvailableException.class,()->borrowService.issueBook(request));
        verify(borrowRecordRepository,never()).save(any(BorrowRecord.class));
    }

    @Test
    void issueBook_shouldFail_whenMemberHasOutstandingFine(){
        Member member = mock(Member.class);
        BookRecord bookRecord = mock(BookRecord.class);
        when(memberService.getMemberById(1L))
                .thenReturn(member);
        when(bookRecordService.getBookRecordById(5L))
                .thenReturn(bookRecord);
        when(bookRecord.isAvailable())
                .thenReturn(true);
        when(member.getOutstandingFine())
                .thenReturn(10L);
        IssueBookRequest request = new IssueBookRequest();
        request.setMemberId(1L);
        request.setBookRecordId(5L);
        OutstandingFineException exception = assertThrows(OutstandingFineException.class,()->borrowService.issueBook(request));
        assertEquals("Member has outstanding fine",exception.getMessage());
        verify(bookRecord,never()).markAsIssued();
        verify(borrowRecordRepository,never()).save(any(BorrowRecord.class));
    }

    @Test
    void issueBook_shouldFail_whenMemberReachedBorrowLimit(){
        Member member = mock(Member.class);
        BookRecord bookRecord = mock(BookRecord.class);
        when(memberService.getMemberById(1L))
                .thenReturn(member);
        when(bookRecordService.getBookRecordById(5L))
                .thenReturn(bookRecord);
        when(bookRecord.isAvailable())
                .thenReturn(true);
        when(member.getOutstandingFine())
                .thenReturn(0L);
        when(member.getMemberId())
                .thenReturn(1L);
        when(borrowRecordRepository.countByMemberMemberIdAndStatus(1L,BorrowStatus.ACTIVE))
                .thenReturn(3L);
        IssueBookRequest request = new IssueBookRequest();
        request.setMemberId(1L);
        request.setBookRecordId(5L);
        BorrowLimitExceededException exception = assertThrows(BorrowLimitExceededException.class,()->borrowService.issueBook(request));
        assertEquals("Member has reached the maximum borrow limit",exception.getMessage());
        verify(bookRecord,never()).markAsIssued();
        verify(borrowRecordRepository,never()).save(any(BorrowRecord.class));
    }

    @Test
    void issueBook_shouldFail_whenMemberDoesNotExist(){
        Member member = mock(Member.class);
        BookRecord bookRecord = mock(BookRecord.class);
        when(memberService.getMemberById(1L))
                .thenThrow(new MemberNotFoundException("Member not found with ID: 1"));
        IssueBookRequest request = new IssueBookRequest();
        request.setMemberId(1L);
        request.setBookRecordId(5L);
        MemberNotFoundException exception = assertThrows(MemberNotFoundException.class,
                ()->borrowService.issueBook(request));
        assertEquals("Member not found with ID: 1",exception.getMessage());
        verify(bookRecord,never()).markAsIssued();
        verify(borrowRecordRepository,never()).save(any(BorrowRecord.class));
    }


    @Test
    void returnBook_shouldReturnBookWithOutFine_whenNotOverdue(){
        Member member = mock(Member.class);
        BookRecord bookRecord = mock(BookRecord.class);
        BorrowRecord borrowRecord =
                new BorrowRecord(member,bookRecord,TEST_DATE.minusDays(5),TEST_DATE.plusDays(9));
        when(borrowRecordRepository.findById(1L))
                .thenReturn(Optional.of(borrowRecord));
        when(member.getMemberId())
                .thenReturn(2L);
        when(bookRecord.getBookRecordId())
                .thenReturn(5L);
        BookReturnResponse response = borrowService.returnBook(1L);
        assertEquals(TEST_DATE,response.getReturnDate());
        assertEquals(TEST_DATE,borrowRecord.getReturnDate());
        assertEquals(0L,response.getFineCharged());
        assertEquals(BorrowStatus.RETURNED,borrowRecord.getStatus());
        verify(member,never()).addFine(anyLong());
        verify(bookRecord).markAsAvailable();
    }

    @Test
    void returnBook_shouldAddFine_whenBookReturnIsOverdue(){
        Member member=mock(Member.class);
        BookRecord bookRecord=mock(BookRecord.class);
        BorrowRecord borrowRecord=
                new BorrowRecord(member,bookRecord,TEST_DATE.minusDays(20),TEST_DATE.minusDays(6));
        when(borrowRecordRepository.findById(1L))
                .thenReturn(Optional.of(borrowRecord));
        when(member.getMemberId())
                .thenReturn(2L);
        when(bookRecord.getBookRecordId())
                .thenReturn(5L);
        BookReturnResponse response = borrowService.returnBook(1L);
        assertEquals(TEST_DATE,response.getReturnDate());
        assertEquals(TEST_DATE,borrowRecord.getReturnDate());
        assertEquals(BorrowStatus.RETURNED,borrowRecord.getStatus());
        assertEquals(30L,response.getFineCharged());
        verify(member).addFine(30L);
        verify(bookRecord).markAsAvailable();
    }

    @Test
    void returnBook_shouldAddFine_whenBorrowRecordDoesNotExist(){
        when(borrowRecordRepository.findById(99L))
                .thenReturn(Optional.empty());
        BorrowRecordNotFoundException exception = assertThrows(BorrowRecordNotFoundException.class,
                ()->borrowService.returnBook(99L));
        assertEquals("Borrow Record not found with ID: 99",exception.getMessage());
    }

    @Test
    void returnBook_shouldFail_whenBorrowRecordAlreadyReturned(){
        Member member=mock(Member.class);
        BookRecord bookRecord = mock(BookRecord.class);
        BorrowRecord borrowRecord =
                new BorrowRecord(member,bookRecord,TEST_DATE.minusDays(20),TEST_DATE.minusDays(6));
        borrowRecord.markAsReturned(TEST_DATE.minusDays(4));
        when(borrowRecordRepository.findById(1L))
                .thenReturn(Optional.of(borrowRecord));
        BorrowRecordAlreadyReturnedException exception = assertThrows(BorrowRecordAlreadyReturnedException.class,
                ()->borrowService.returnBook(1L));
        assertEquals("Borrow record is already returned",exception.getMessage());
        assertEquals(TEST_DATE.minusDays(4),borrowRecord.getReturnDate());
        verify(bookRecord,never()).markAsAvailable();
        verify(member,never()).addFine(anyLong());
    }

    @Test
    void getOverdueBorrowRecords_shouldReturnMappedOverdueResponses(){
        Member member1 = new Member("hima","hima@gmail.com","9191919191");
        Book book = new Book("Sample Book","Sample Author","Sample Publisher",2011,2);
        BookRecord bookRecord1 = new BookRecord(book);
        BorrowRecord borrowRecord=
                new BorrowRecord(member1,bookRecord1,TEST_DATE.minusDays(20),TEST_DATE.minusDays(6));
        when(borrowRecordRepository.findByStatusAndDueDateBefore(BorrowStatus.ACTIVE,TEST_DATE))
                .thenReturn(List.of(borrowRecord));
        List<OverdueBorrowResponse> responses = borrowService.getOverdueBorrowRecords();
        assertEquals(1,responses.size());
        OverdueBorrowResponse response = responses.getFirst();
        assertEquals("hima",response.getMemberName());
        assertEquals("Sample Book",response.getBookTitle());
        assertEquals(TEST_DATE.minusDays(6),response.getDueDate());
        assertEquals(6L,response.getOverdueDays());
        assertEquals(30L,response.getAccruedFine());
    }

    @Test
    void getAllBorrowRecords_shouldReturnMappedBorrowRecordResponses(){
        Member member1 = new Member("hima","hima@gmail.com","9191919191");
        Book book = new Book("Sample Book","Sample Author","Sample Publisher",2011,2);
        BookRecord bookRecord1 = new BookRecord(book);
        BorrowRecord borrowRecord=
                new BorrowRecord(member1,bookRecord1,TEST_DATE.minusDays(20),TEST_DATE.minusDays(6));
        borrowRecord.markAsReturned(TEST_DATE);
        when(borrowRecordRepository.findAll())
                .thenReturn(List.of(borrowRecord));
        List<BorrowRecordResponse> responses = borrowService.getAllBorrowRecords();
        assertEquals(1,responses.size());
        BorrowRecordResponse response = responses.getFirst();
        assertEquals("hima",response.getMemberName());
        assertEquals("Sample Book",response.getBookTitle());
        assertEquals(TEST_DATE.minusDays(20),response.getBorrowDate());
        assertEquals(TEST_DATE.minusDays(6),response.getDueDate());
        assertEquals(TEST_DATE,response.getReturnDate());
        assertEquals(BorrowStatus.RETURNED,response.getStatus());
    }

    @Test
    void voidBorrowRecord_shouldVoidBorrowRecord_whenBorrowRecordIsActive(){
        Member member=mock(Member.class);
        Book book = new Book("Sample Book","Sample Author","Sample Publisher",2011,2);
        BookRecord bookRecord = new BookRecord(book);
        BorrowRecord borrowRecord =
                new BorrowRecord(member,bookRecord,TEST_DATE,TEST_DATE.plusDays(14));
        when(borrowRecordRepository.findById(1L))
                .thenReturn(Optional.of(borrowRecord));
        borrowService.voidBorrowRecord(1L,TEST_VOIDED_BY,TEST_VOID_REASON);
        assertEquals(BorrowStatus.VOIDED,borrowRecord.getStatus());
        assertEquals(BookRecordStatus.AVAILABLE,bookRecord.getStatus());
        assertEquals(TEST_VOIDED_BY,borrowRecord.getVoidedBy());
        assertEquals(TEST_VOID_REASON,borrowRecord.getVoidReason());
        assertEquals(TEST_DATE.atStartOfDay(),borrowRecord.getVoidedAt());
        verify(borrowRecordRepository).findById(1L);
    }

    @Test
    void voidBorrowRecord_shouldThrowException_whenRecordIsAlreadyReturned(){
        Member member=mock(Member.class);
        //Book book = new Book("Sample Book","Sample Author","Sample Publisher",2011,2);
        BookRecord bookRecord = mock(BookRecord.class);
        BorrowRecord borrowRecord =
                new BorrowRecord(member,bookRecord,TEST_DATE.minusDays(15),TEST_DATE.plusDays(1));
        borrowRecord.markAsReturned(TEST_DATE.minusDays(2));
        when(borrowRecordRepository.findById(1L))
                .thenReturn(Optional.of(borrowRecord));
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                ()->borrowService.voidBorrowRecord(1L,TEST_VOIDED_BY,TEST_VOID_REASON));
        assertEquals("Returned Borrow Record cannot be voided",exception.getMessage());
        assertEquals(BorrowStatus.RETURNED,borrowRecord.getStatus());
        assertNull(borrowRecord.getVoidedBy());
        assertNull(borrowRecord.getVoidReason());
        assertNull(borrowRecord.getVoidedAt());
        verify(borrowRecordRepository).findById(1L);
        verify(bookRecord,never()).markAsAvailable();
    }

    @Test
    void voidBorrowRecord_shouldThrowException_whenRecordIsAlreadyVoided(){
        Member member=mock(Member.class);
        BookRecord bookRecord = mock(BookRecord.class);
        BorrowRecord borrowRecord =
                new BorrowRecord(member,bookRecord,TEST_DATE.minusDays(15),TEST_DATE.plusDays(1));
        borrowRecord.markAsVoided(TEST_VOIDED_BY,TEST_VOID_REASON,TEST_DATE.atStartOfDay());
        when(borrowRecordRepository.findById(1L))
                .thenReturn(Optional.of(borrowRecord));
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                ()->borrowService.voidBorrowRecord(1L,TEST_VOIDED_BY,TEST_VOID_REASON));
        assertEquals("Borrow Record is already voided",exception.getMessage());
        verify(borrowRecordRepository).findById(1L);
        verify(bookRecord,never()).markAsAvailable();
    }
}
