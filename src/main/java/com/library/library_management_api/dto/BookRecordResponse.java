package com.library.library_management_api.dto;

import com.library.library_management_api.model.BookRecordStatus;

import java.time.LocalDate;

public record BookRecordResponse(
        Long bookRecordId,
        Long bookId,
        String bookTitle,
        BookRecordStatus status,
        LocalDate acquiredDate
) { }
