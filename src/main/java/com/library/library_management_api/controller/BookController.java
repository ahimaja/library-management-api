package com.library.library_management_api.controller;

import com.library.library_management_api.dto.BookResponse;
import com.library.library_management_api.dto.CreateBookRequest;
import com.library.library_management_api.dto.UpdateBookRequest;
import com.library.library_management_api.model.Book;
import com.library.library_management_api.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
@SecurityRequirement(name = "bearerAuth")
@Tag(name="Books", description = "Operations for managing books information")
@ApiResponses({
        @ApiResponse(responseCode = "401", description = "Authentication required or JWT is invalid")
})
public class BookController {


    private final BookService bookService;

    public BookController(BookService bookService){
        this.bookService=bookService;
    }


    @Operation(summary = "Create a new book",
            description = "Requires EMPLOYEE or ADMIN role" )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Book created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid book data"),
            @ApiResponse(responseCode = "403", description = "User does not have the required role")
    })
    @PostMapping
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody CreateBookRequest request){
        BookResponse creaatedBookResponse = bookService.createBook(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creaatedBookResponse);
        //body() does two things: attaches the body and finishes building the response
    }




    @Operation(summary = "Get all the books",
            description = "Lists all the books in the library. Requires MEMBER, EMPLOYEE or ADMIN role" )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Books retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<List<BookResponse>> getAllBooks(){
        return ResponseEntity.status(HttpStatus.OK).body(bookService.getAllBooks());
    }




    @Operation(summary = "Get a book by entering the book id",
            description = "Requires MEMBER, EMPLOYEE or ADMIN role" )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book with the specified book id retrieved successfully")
    })
    @GetMapping("/{bookId}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable("bookId") Long bookId){
        return ResponseEntity.status(HttpStatus.OK).body(bookService.getBookResponseById(bookId));
        //return ResponseEntity.ok(bookService.getBookResponseById(bookId)); //diff format to write
    }




    @Operation(summary = "Update an existing book",
            description = "Requires EMPLOYEE or ADMIN role" )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid book data"),
            @ApiResponse(responseCode = "403", description = "User does not have the required role")
    })
    @PutMapping("/{bookId}")
    public ResponseEntity<BookResponse> updateBook(@PathVariable("bookId") Long bookId,
                                           @Valid @RequestBody UpdateBookRequest request){
        BookResponse updatedBookResponse = bookService.updateBook(bookId,request);
        return ResponseEntity.status(HttpStatus.OK).body(updatedBookResponse);
    }



    @Operation(summary = "Delete an existing book",
            description = "Book having physical copies cannot be deleted. Requires EMPLOYEE or ADMIN role" )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No content to retrieve"),
    })

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> deleteBook(@PathVariable("bookId") Long bookId){
        bookService.deleteBook(bookId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        //return ResponseEntity.noContent().build();
        //delete->204 No Content should not contain a body
    }



    @Operation(summary = "Search for book/books by entering a keyword",
            description = "Requires MEMBER or EMPLOYEE or ADMIN role" )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search completed successfully"),
    })
    @GetMapping("/search")
    public ResponseEntity<List<BookResponse>> searchBooks(@RequestParam String keyword){
        return ResponseEntity.status(HttpStatus.OK).body(bookService.searchBooks(keyword));
    }
}
