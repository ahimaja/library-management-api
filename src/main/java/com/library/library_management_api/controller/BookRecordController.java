package com.library.library_management_api.controller;

import com.library.library_management_api.dto.BookRecordResponse;
import com.library.library_management_api.dto.CreateBookRecordRequest;
import com.library.library_management_api.model.BookRecord;
import com.library.library_management_api.service.BookRecordService;
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
@RequestMapping("book-records")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Book Records", description = "Operations for managing physical copies of books")
@ApiResponses({
        @ApiResponse(responseCode = "400", description = "Authentication required or JWT is invalid")
})
public class BookRecordController {

    private final BookRecordService bookRecordService;

    public BookRecordController(BookRecordService bookRecordService){
        this.bookRecordService=bookRecordService;
    }

    @Operation(summary = "Create a new Book copy",
            description = "Adds a physical copy of a book. Requires EMPLOYEE or ADMIN role")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Book copy creation successful"),
            @ApiResponse(responseCode = "400", description = "Invalid copy creation request data"),
            @ApiResponse(responseCode = "403", description = "User does not have the required role")
    })
    @PostMapping
    public ResponseEntity<BookRecordResponse> createBookRecord(@Valid @RequestBody CreateBookRecordRequest request){
        BookRecordResponse createdBookRecordResponse = bookRecordService.createBookRecord(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBookRecordResponse);
    }

    @Operation(summary = "Gets all the book copies",
            description = "Lists all the book copies in the library. Requires MEMBER, EMPLOYEE or ADMIN role" )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book copies retrieved successfully"),
    })
    @GetMapping
    public ResponseEntity<List<BookRecordResponse>> getAllBookRecords(){
        return ResponseEntity.ok(bookRecordService.getAllBookRecords());
    }

    @Operation(summary = "Get a book copy by entering the book record id",
            description = "Requires MEMBER, EMPLOYEE or ADMIN role" )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Book copy with the specified book record id retrieved successfully"),
    })
    @GetMapping("/{id}")
    public ResponseEntity<BookRecordResponse> getBookRecordById(@PathVariable("id") Long bookRecordId){
        return ResponseEntity.ok(bookRecordService.getBookRecordResponseById(bookRecordId));
    }

    @Operation(summary = "Get all the physical copies of a particular book by entering its book id",
            description = "Requires MEMBER, EMPLOYEE or ADMIN role" )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Book copies with the specified book id retrieved successfully"),
    })
    @GetMapping("/book/{id}")
    public ResponseEntity<List<BookRecordResponse>> getBookRecordsByBookId(@PathVariable("id") Long bookId){
        return ResponseEntity.ok(bookRecordService.getBookRecordsByBookId(bookId));
    }

    @Operation(summary = "Retire a Book copy",
            description = "Retires a physical book copy. An issued copy cannot be retired. Requires MEMBER, EMPLOYEE or ADMIN role" )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No content to retrieve"),
    })
    @PatchMapping("/{id}/retire")
    public ResponseEntity<Void> retireBookRecord(@PathVariable("id") Long bookRecordId){
        bookRecordService.retireBookRecord(bookRecordId);
        return ResponseEntity.noContent().build();
    }
}
