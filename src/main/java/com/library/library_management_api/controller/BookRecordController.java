package com.library.library_management_api.controller;

import com.library.library_management_api.dto.BookRecordResponse;
import com.library.library_management_api.dto.CreateBookRecordRequest;
import com.library.library_management_api.model.BookRecord;
import com.library.library_management_api.service.BookRecordService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("book-records")
public class BookRecordController {

    private final BookRecordService bookRecordService;

    public BookRecordController(BookRecordService bookRecordService){
        this.bookRecordService=bookRecordService;
    }

    @PostMapping
    public ResponseEntity<BookRecordResponse> createBookRecord(@Valid @RequestBody CreateBookRecordRequest request){
        BookRecordResponse createdBookRecordResponse = bookRecordService.createBookRecord(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBookRecordResponse);
    }

    @GetMapping
    public ResponseEntity<List<BookRecordResponse>> getAllBookRecords(){
        return ResponseEntity.ok(bookRecordService.getAllBookRecords());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookRecordResponse> getBookRecordById(@PathVariable("id") Long bookRecordId){
        return ResponseEntity.ok(bookRecordService.getBookRecordResponseById(bookRecordId));
    }

    @GetMapping("/book/{id}")
    public ResponseEntity<List<BookRecordResponse>> getBookRecordsByBookId(@PathVariable("id") Long bookId){
        return ResponseEntity.ok(bookRecordService.getBookRecordsByBookId(bookId));
    }

    @PatchMapping("/{id}/retire")
    public ResponseEntity<Void> retireBookRecord(@PathVariable("id") Long bookRecordId){
        bookRecordService.retireBookRecord(bookRecordId);
        return ResponseEntity.noContent().build();
    }
}
