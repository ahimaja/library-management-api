package com.library.library_management_api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class UpdateBookRequest {
    @NotNull(message = "Title is required")
    private String title;

    @NotNull(message = "Author name is required")
    private String author;

    @NotNull(message = "Publisher is required")
    private String publisher;

    @Min(value = 1000,message = "Publication year must be at least 1000")
    private int publicationYear;

    @Positive(message = "Edition must be greater than zero")
    private int edition;

    public UpdateBookRequest(){

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }

    public int getEdition() {
        return edition;
    }

    public void setEdition(int edition) {
        this.edition = edition;
    }
}
