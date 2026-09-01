package com.library.library_management_api.controller;

import com.library.library_management_api.dto.BookResponse;
import com.library.library_management_api.dto.CreateBookRequest;
import com.library.library_management_api.dto.UpdateBookRequest;
import com.library.library_management_api.model.Book;
import com.library.library_management_api.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {


    private final BookService bookService;

    public BookController(BookService bookService){
        this.bookService=bookService;
    }

    @PostMapping
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody CreateBookRequest request){
        BookResponse creaatedBookResponse = bookService.createBook(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creaatedBookResponse);
        //body() does two things: attaches the body and finishes building the response
    }

    @GetMapping
    public ResponseEntity<List<BookResponse>> getAllBooks(){
        return ResponseEntity.status(HttpStatus.OK).body(bookService.getAllBooks());
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable("bookId") Long bookId){
        return ResponseEntity.status(HttpStatus.OK).body(bookService.getBookResponseById(bookId));
        //return ResponseEntity.ok(bookService.getBookResponseById(bookId)); //diff format to write
    }

    @PutMapping("/{bookId}")
    public ResponseEntity<BookResponse> updateBook(@PathVariable("bookId") Long bookId,
                                           @Valid @RequestBody UpdateBookRequest request){
        BookResponse updatedBookResponse = bookService.updateBook(bookId,request);
        return ResponseEntity.status(HttpStatus.OK).body(updatedBookResponse);
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> deleteBook(@PathVariable("bookId") Long bookId){
        bookService.deleteBook(bookId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        //return ResponseEntity.noContent().build();
        //delete->204 No Content should not contain a body
    }

    @GetMapping("/search")
    public ResponseEntity<List<BookResponse>> searchBooks(@RequestParam String keyword){
        return ResponseEntity.status(HttpStatus.OK).body(bookService.searchBooks(keyword));
    }
}
