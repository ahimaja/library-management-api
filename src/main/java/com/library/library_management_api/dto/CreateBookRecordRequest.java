package com.library.library_management_api.dto;

import jakarta.validation.constraints.NotNull;

public class CreateBookRecordRequest {

    @NotNull(message = "Book ID is required")
    private Long bookId;

    public CreateBookRecordRequest() {
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }
}
