package com.library.library_management_api.service;

import com.library.library_management_api.dto.BookRecordResponse;
import com.library.library_management_api.dto.CreateBookRecordRequest;
import com.library.library_management_api.exception.BookRecordNotFoundException;
import com.library.library_management_api.model.Book;
import com.library.library_management_api.model.BookRecord;
import com.library.library_management_api.repository.BookRecordRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookRecordService {

    private final BookRecordRepository bookRecordRepository;
    private final BookService bookService;

    public BookRecordService(BookRecordRepository bookRecordRepository,
                             BookService bookService){
        this.bookRecordRepository=bookRecordRepository;
        this.bookService=bookService;
    }

    public BookRecordResponse createBookRecord(CreateBookRecordRequest request){
        Book book = bookService.getBookById(request.getBookId());
        BookRecord bookRecord = new BookRecord(book,request.getAcquiredDate());
        BookRecord savedBookRecord = bookRecordRepository.save(bookRecord);
        return toResponse(savedBookRecord);
    }

    private BookRecordResponse toResponse(BookRecord bookRecord){
        return new BookRecordResponse(
                bookRecord.getBookRecordId(),
                bookRecord.getBook().getBookId(),
                bookRecord.getBook().getTitle(),
                bookRecord.getStatus(),
                bookRecord.getAcquiredDate());
    }

    public BookRecord getBookRecordById(Long bookRecordId){
        return bookRecordRepository.findById(bookRecordId)
                .orElseThrow(()->new BookRecordNotFoundException("Book Record not found with ID: "+bookRecordId));
    }

    public BookRecordResponse getBookRecordResponseById(Long bookRecordId){
        return toResponse(getBookRecordById(bookRecordId));
    }

    public List<BookRecordResponse> getAllBookRecords(){
        return bookRecordRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<BookRecordResponse> getBookRecordsByBookId(Long bookId){
        return bookRecordRepository.findByBookBookId(bookId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    // with @Transactional , bookRecordRepository.save(bookRecord) can be skipped
    public void retireBookRecord(Long bookRecordId){
        BookRecord bookRecord = getBookRecordById(bookRecordId);
        bookRecord.markAsRetired();
    }
}
