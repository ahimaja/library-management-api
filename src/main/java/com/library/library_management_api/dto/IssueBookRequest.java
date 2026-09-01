package com.library.library_management_api.dto;

import jakarta.validation.constraints.NotNull;

public class IssueBookRequest {

    @NotNull(message = "Member ID is required")
    private Long memberId;

    @NotNull(message = "Book Record ID is required")
    private Long bookRecordId;

    public IssueBookRequest() {
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getBookRecordId() {
        return bookRecordId;
    }

    public void setBookRecordId(Long bookRecordId) {
        this.bookRecordId = bookRecordId;
    }
}
