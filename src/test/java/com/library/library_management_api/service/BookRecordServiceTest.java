package com.library.library_management_api.service;

import com.library.library_management_api.dto.BookRecordResponse;
import com.library.library_management_api.dto.CreateBookRecordRequest;
import com.library.library_management_api.exception.BookRecordNotFoundException;
import com.library.library_management_api.model.Book;
import com.library.library_management_api.model.BookRecord;
import com.library.library_management_api.model.BookRecordStatus;
import com.library.library_management_api.repository.BookRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookRecordServiceTest {

    private BookRecordService bookRecordService;

    @Mock
    private BookRecordRepository bookRecordRepository;

    @Mock
    private BookService bookService;

    @BeforeEach
    void setUp(){
        bookRecordService=new BookRecordService(bookRecordRepository,bookService);
    }

    @Test
    void createBookRecord_shouldCreateRecord_whenBookIsAvailable(){
        Book book = new Book("Test Book","Test Author","Test Publisher",2000,2);
        when(bookService.getBookById(2L))
                .thenReturn(book);
        when(bookRecordRepository.save(any(BookRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        CreateBookRecordRequest request= new CreateBookRecordRequest();
        request.setBookId(2L);
        BookRecordResponse resultBookRecordResponse = bookRecordService.createBookRecord(request);
        assertEquals(BookRecordStatus.AVAILABLE,resultBookRecordResponse.status());
        assertEquals("Test Book",resultBookRecordResponse.bookTitle());
        verify(bookRecordRepository).save(any(BookRecord.class));
    }

    @Test
    void getBookRecordById_shouldFail_whenRecordDoesNotExist(){
        when(bookRecordRepository.findById(2L))
                .thenReturn(Optional.empty());
        BookRecordNotFoundException exception = assertThrows(BookRecordNotFoundException.class,
                ()->bookRecordService.getBookRecordById(2L));
        assertEquals("Book Record not found with ID: 2",exception.getMessage());
    }

    @Test
    void retireBookRecord_shouldRetireRecord_whenRecordIsAvailable(){
        Book book = new Book("Test Book","Test Author","Test Publisher",2000,2);
        BookRecord bookRecord=new BookRecord(book);
        when(bookRecordRepository.findById(3L))
                .thenReturn(Optional.of(bookRecord));
        bookRecordService.retireBookRecord(3L);
        assertEquals(BookRecordStatus.RETIRED,bookRecord.getStatus());
        verify(bookRecordRepository).findById(3L);
    }

    @Test
    void retireBookRecord_shouldThrowException_whenRecordIsIssued(){
        Book book = new Book("Test Book","Test Author","Test Publisher",2000,2);
        BookRecord bookRecord =new BookRecord(book);
        bookRecord.markAsIssued();
        when(bookRecordRepository.findById(3L))
                .thenReturn(Optional.of(bookRecord));
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                ()->bookRecordService.retireBookRecord(3L));
        assertEquals("Issued Book record cannot be retired",exception.getMessage());
        assertEquals(BookRecordStatus.ISSUED,bookRecord.getStatus());
    }

    @Test
    void retireBookRecord_shouldThrowException_whenRecordIsAlreadyRetired(){
        Book book = new Book("Test Book","Test Author","Test Publisher",2000,2);
        BookRecord bookRecord =new BookRecord(book);
        bookRecord.markAsRetired();
        when(bookRecordRepository.findById(3L))
                .thenReturn(Optional.of(bookRecord));
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                ()->bookRecordService.retireBookRecord(3L));
        assertEquals("Book Record is already retired",exception.getMessage());
        assertEquals(BookRecordStatus.RETIRED,bookRecord.getStatus());
    }

}
