package com.library.library_management_api.dto;


import com.library.library_management_api.model.BorrowStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class BorrowRecordResponse {

    private final Long borrowId;
    private final Long memberId;
    private final String memberName;
    private final Long bookRecordId;
    private final String bookTitle;
    private final LocalDate borrowDate;
    private final LocalDate dueDate;
    private final LocalDate returnDate;
    private final BorrowStatus status;
    private final String voidedBy;
    private final String voidReason;
    private final LocalDateTime voidedAt;

    public BorrowRecordResponse(Long borrowId, Long memberId, String memberName,
                                Long bookRecordId, String bookTitle, LocalDate borrowDate,
                                LocalDate dueDate, LocalDate returnDate, BorrowStatus status,
                                String voidedBy,String voidReason,LocalDateTime voidedAt) {
        this.borrowId = borrowId;
        this.memberId = memberId;
        this.memberName = memberName;
        this.bookRecordId = bookRecordId;
        this.bookTitle = bookTitle;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.status = status;
        this.voidedBy=voidedBy;
        this.voidReason=voidReason;
        this.voidedAt=voidedAt;
    }

    public Long getBorrowId() {
        return borrowId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public Long getBookRecordId() {
        return bookRecordId;
    }

    public String getBookTitle() {
        return bookTitle;
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
}
