package com.library.library_management_api.dto;

import java.time.LocalDate;

public class BookReturnResponse {

    private final Long borrowId;
    private final Long memberID;
    private final Long bookRecordId;
    private final LocalDate returnDate;
    private final long fineCharged;

    public BookReturnResponse(Long borrowId, Long memberID, Long bookRecordId, LocalDate returnDate, long fineCharged) {
        this.borrowId = borrowId;
        this.memberID = memberID;
        this.bookRecordId = bookRecordId;
        this.returnDate = returnDate;
        this.fineCharged = fineCharged;
    }

    public Long getBorrowId() {
        return borrowId;
    }

    public Long getMemberID() {
        return memberID;
    }

    public Long getBookRecordId() {
        return bookRecordId;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public long getFineCharged() {
        return fineCharged;
    }
}
