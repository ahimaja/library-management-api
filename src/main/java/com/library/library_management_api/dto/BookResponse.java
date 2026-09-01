package com.library.library_management_api.dto;

public record BookResponse(Long bookId,
                           String title,
                           String author,
                           String publisher,
                           int publicationYear,
                           int edition) {
}
