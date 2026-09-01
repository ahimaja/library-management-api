package com.library.library_management_api.dto;

import java.time.LocalDate;

public class OverdueBorrowResponse {

    private final Long borrowId;
    private final Long memberId;
    private final Long bookRecordId;
    private final String memberName;
    private final String bookTitle;
    private final LocalDate dueDate;
    private final long overdueDays;
    private final long accruedFine;

    public OverdueBorrowResponse(Long borrowId, Long memberId, Long bookRecordId, String memberName, String bookTitle, LocalDate dueDate, long overdueDays, long accruedFine) {
        this.borrowId = borrowId;
        this.memberId = memberId;
        this.bookRecordId = bookRecordId;
        this.memberName = memberName;
        this.bookTitle = bookTitle;
        this.dueDate = dueDate;
        this.overdueDays = overdueDays;
        this.accruedFine = accruedFine;
    }

    public Long getBorrowId() {
        return borrowId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public Long getBookRecordId() {
        return bookRecordId;
    }

    public String getMemberName() {
        return memberName;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public long getOverdueDays() {
        return overdueDays;
    }

    public long getAccruedFine() {
        return accruedFine;
    }
}
