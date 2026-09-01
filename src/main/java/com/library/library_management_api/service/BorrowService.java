package com.library.library_management_api.service;

import com.library.library_management_api.dto.BookReturnResponse;
import com.library.library_management_api.dto.BorrowRecordResponse;
import com.library.library_management_api.dto.IssueBookRequest;
import com.library.library_management_api.dto.OverdueBorrowResponse;
import com.library.library_management_api.exception.BookRecordNotAvailableException;
import com.library.library_management_api.exception.BorrowLimitExceededException;
import com.library.library_management_api.exception.BorrowRecordNotFoundException;
import com.library.library_management_api.exception.OutstandingFineException;
import com.library.library_management_api.model.*;
import com.library.library_management_api.repository.BorrowRecordRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BorrowService {

    private static final int BORROW_PERIOD_DAYS = 14;
    private static final int MAX_ACTIVE_BORROWS = 3;
    private static final long FINE_PER_DAY = 5;

    private final BorrowRecordRepository borrowRecordRepository;
    private final MemberService memberService;
    private final BookRecordService bookRecordService;
    private final Clock clock;

    public BorrowService(BorrowRecordRepository borrowRecordRepository,
                         MemberService memberService,
                         BookRecordService bookRecordService,
                         Clock clock){
        this.borrowRecordRepository=borrowRecordRepository;
        this.memberService=memberService;
        this.bookRecordService=bookRecordService;
        this.clock=clock;
    }

    @Transactional
    public BorrowRecordResponse issueBook(IssueBookRequest request){
        Member member = memberService.getMemberById(request.getMemberId());
        BookRecord bookRecord = bookRecordService.getBookRecordById(request.getBookRecordId());
        if(!bookRecord.isAvailable())
            throw new BookRecordNotAvailableException("Book Record not available");
        if(member.getStatus()== MemberStatus.INACTIVE)
            throw new IllegalStateException("Inactive member cannot borrow books");
        if(member.getOutstandingFine()>0)
            throw new OutstandingFineException("Member has outstanding fine");
        long activeBorrowCount = borrowRecordRepository
                .countByMemberMemberIdAndStatus(member.getMemberId(), BorrowStatus.ACTIVE);
        if(activeBorrowCount>=MAX_ACTIVE_BORROWS)
            throw new BorrowLimitExceededException("Member has reached the maximum borrow limit");
        LocalDate borrowDate = LocalDate.now(clock);
        LocalDate dueDate = borrowDate.plusDays(BORROW_PERIOD_DAYS);
        bookRecord.markAsIssued();
        BorrowRecord borrowRecord = new BorrowRecord(member,bookRecord,borrowDate,dueDate);
        borrowRecordRepository.save(borrowRecord);
        return toResponse(borrowRecord);
    }

    public BorrowRecordResponse toResponse(BorrowRecord borrowRecord){
        return new BorrowRecordResponse(borrowRecord.getBorrowId(),
                borrowRecord.getMember().getMemberId(),
                borrowRecord.getMember().getName(),
                borrowRecord.getBookRecord().getBookRecordId(),
                borrowRecord.getBookRecord().getBook().getTitle(),
                borrowRecord.getBorrowDate(),
                borrowRecord.getDueDate(),
                borrowRecord.getReturnDate(),
                borrowRecord.getStatus(),
                borrowRecord.getVoidedBy(),
                borrowRecord.getVoidReason(),
                borrowRecord.getVoidedAt());
    }

    @Transactional
    public BookReturnResponse returnBook(Long borrowId){
        BorrowRecord borrowRecord = getBorrowRecordById(borrowId);
        LocalDate returnDate = LocalDate.now(clock);
        long fineCharged = borrowRecord.calculateFine(FINE_PER_DAY,returnDate);
        borrowRecord.markAsReturned(returnDate);
        borrowRecord.getBookRecord().markAsAvailable();
        if(fineCharged>0){
            borrowRecord.getMember().addFine(fineCharged);
        }
        return new BookReturnResponse(borrowId,
                borrowRecord.getMember().getMemberId(),
                borrowRecord.getBookRecord().getBookRecordId(),
                returnDate,
                fineCharged);
    }

    private BorrowRecord getBorrowRecordById(Long borrowId){
        return borrowRecordRepository.findById(borrowId)
                .orElseThrow(()->new BorrowRecordNotFoundException("Borrow Record not found with ID: "+borrowId));
    }

    public BorrowRecordResponse getBorrowRecordResponseById(Long borrowId){
        return toResponse(getBorrowRecordById(borrowId));
    }


    public List<BorrowRecordResponse> getAllBorrowRecords(){
        return borrowRecordRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<BorrowRecordResponse> getBorrowHistoryByMemberId(Long memberId){
        memberService.getMemberById(memberId); // checks if member exists
        return borrowRecordRepository.findByMemberMemberId(memberId).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<BorrowRecordResponse> getActiveBorrowsByMemberId(Long memberId){
        memberService.getMemberById(memberId); // checks if member exists
        return borrowRecordRepository.findByMemberMemberIdAndStatus(memberId,BorrowStatus.ACTIVE).stream()
                .map(this::toResponse)
                .toList();
    }

    private OverdueBorrowResponse toOverdueResponse(BorrowRecord borrowRecord,LocalDate currentDate){
        return new OverdueBorrowResponse(borrowRecord.getBorrowId(),
                borrowRecord.getMember().getMemberId(),
                borrowRecord.getBookRecord().getBookRecordId(),
                borrowRecord.getMember().getName(),
                borrowRecord.getBookRecord().getBook().getTitle(),
                borrowRecord.getDueDate(),
                borrowRecord.getOverdueDays(currentDate),
                borrowRecord.calculateFine(FINE_PER_DAY,currentDate));
    }

    public List<OverdueBorrowResponse> getOverdueBorrowRecords(){
        LocalDate currentDate = LocalDate.now(clock);
        return borrowRecordRepository.findByStatusAndDueDateBefore(BorrowStatus.ACTIVE,currentDate)
                .stream()
                .map(borrowRecord -> toOverdueResponse(borrowRecord,currentDate))
                .toList();
    }

    @Transactional
    public void voidBorrowRecord(Long borrowId,String voidedBy, String voidReason){
        BorrowRecord borrowRecord = getBorrowRecordById(borrowId);
        LocalDateTime voidedAt = LocalDateTime.now(clock);
        borrowRecord.markAsVoided(voidedBy,voidReason,voidedAt);
        borrowRecord.getBookRecord().markAsAvailable();
    }
}
