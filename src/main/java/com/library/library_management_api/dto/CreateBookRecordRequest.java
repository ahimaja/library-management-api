package com.library.library_management_api.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class CreateBookRecordRequest {

    @NotNull(message = "Book ID is required")
    private Long bookId;

    @NotNull(message = "Acquired date is required")
    private LocalDate acquiredDate;

    public CreateBookRecordRequest() {
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public LocalDate getAcquiredDate() {
        return acquiredDate;
    }

    public void setAcquiredDate(LocalDate acquiredDate) {
        this.acquiredDate = acquiredDate;
    }
}
