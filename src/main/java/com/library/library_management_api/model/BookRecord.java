package com.library.library_management_api.model;

import com.library.library_management_api.exception.BookRecordNotAvailableException;
import jakarta.persistence.*;

@Entity
@Table(name="book_records")
public class BookRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookRecordId;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Enumerated(EnumType.STRING)
    private BookRecordStatus status;

    protected BookRecord(){

    }

    public BookRecord(Book book) {
        this.book = book;
        this.status=BookRecordStatus.AVAILABLE;
    }

    public Long getBookRecordId() {
        return bookRecordId;
    }


    public Book getBook() {
        return book;
    }


    public BookRecordStatus getStatus() {
        return status;
    }

    public void markAsIssued(){
        if(status==BookRecordStatus.ISSUED)
            throw new BookRecordNotAvailableException("Book record is already issued");
        status=BookRecordStatus.ISSUED;
    }

    public void markAsRetired(){
        if(status==BookRecordStatus.ISSUED)
            throw new IllegalStateException("Issued Book record cannot be retired");
        if(status==BookRecordStatus.RETIRED)
            throw new IllegalStateException("Book Record is already retired");
        status=BookRecordStatus.RETIRED;
    }

    public void markAsAvailable(){
        status=BookRecordStatus.AVAILABLE;
    }

    public boolean isAvailable(){
        return status==BookRecordStatus.AVAILABLE;
    }

}
