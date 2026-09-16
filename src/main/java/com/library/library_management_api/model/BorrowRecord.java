package com.library.library_management_api.model;

import com.library.library_management_api.exception.BorrowRecordAlreadyReturnedException;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "borrow_records")
public class BorrowRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long borrowId;

    @ManyToOne
    @JoinColumn(name = "member_id",nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "book_record_id",nullable = false)
    private BookRecord bookRecord;

    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BorrowStatus status;

    private String voidedBy;
    private String voidReason;
    private LocalDateTime voidedAt;

    protected BorrowRecord(){

    }

    public BorrowRecord(Member member, BookRecord bookRecord, LocalDate borrowDate, LocalDate dueDate) {
        this.member = member;
        this.bookRecord = bookRecord;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.status=BorrowStatus.ACTIVE;
    }

    public Long getBorrowId() {
        return borrowId;
    }

    public Member getMember() {
        return member;
    }

    public BookRecord getBookRecord() {
        return bookRecord;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }


    public LocalDate getDueDate() {
        return dueDate;
    }


    public LocalDate getReturnDate() {
        return returnDate;
    }

    public BorrowStatus getStatus() {
        return status;
    }

    public String getVoidedBy() {
        return voidedBy;
    }

    public String getVoidReason() {
        return voidReason;
    }

    public LocalDateTime getVoidedAt() {
        return voidedAt;
    }

    public void markAsReturned(LocalDate returnDate){
        if(status==BorrowStatus.RETURNED)
            throw new BorrowRecordAlreadyReturnedException("Borrow record is already returned");
        if(status==BorrowStatus.VOIDED)
            throw new IllegalStateException("Voided borrow record cannot be returned");
        status=BorrowStatus.RETURNED;
        this.returnDate=returnDate;
    }

    public void markAsVoided(String voidedBy, String voidReason, LocalDateTime voidedAt){
        if(status==BorrowStatus.RETURNED)
            throw new IllegalStateException("Returned Borrow Record cannot be voided");
        if(status==BorrowStatus.VOIDED)
            throw new IllegalStateException("Borrow Record is already voided");
        status=BorrowStatus.VOIDED;
        this.voidedBy=voidedBy;
        this.voidReason=voidReason;
        this.voidedAt=voidedAt;
    }

    public boolean isOverdue(LocalDate currentDate){
        return status==BorrowStatus.ACTIVE && currentDate.isAfter(dueDate);
    }

    public long getOverdueDays(LocalDate currentDate){
        if(!isOverdue(currentDate))
            return 0;
        return ChronoUnit.DAYS.between(dueDate,currentDate);
    }

    public long calculateFine(long finePerDay, LocalDate currentDate){
        return finePerDay*getOverdueDays(currentDate);
    }
}
