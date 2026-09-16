package com.library.library_management_api.service;

import com.library.library_management_api.dto.BookResponse;
import com.library.library_management_api.dto.CreateBookRequest;
import com.library.library_management_api.dto.UpdateBookRequest;
import com.library.library_management_api.exception.BookNotFoundException;
import com.library.library_management_api.exception.DuplicateBookException;
import com.library.library_management_api.model.Book;
import com.library.library_management_api.repository.BookRecordRepository;
import com.library.library_management_api.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    BookRepository bookRepository;

    @Mock
    BookRecordRepository bookRecordRepository;

    private BookService bookService;

    @BeforeEach
    void setup(){
        bookService=new BookService(bookRepository,bookRecordRepository);
    }

    @Test
    void getBookById_shouldFail_whenBookDoesNotExist(){
        when(bookRepository.findById(1L))
                .thenReturn(Optional.empty());
        BookNotFoundException exception = assertThrows(BookNotFoundException.class,
                ()->bookService.getBookById(1L));
        assertEquals("Book not found with ID: 1",exception.getMessage());
    }

    @Test
    void createBook_shouldFail_whenDuplicateBookExists(){
        CreateBookRequest request = new CreateBookRequest();
        request.setTitle("Test Book");
        request.setAuthor("Test Author");
        request.setPublisher("Test publisher");
        request.setPublicationYear(2010);
        request.setEdition(2);
        request.setIsbn("1FB3KRB3BKETBKB");
        when(bookRepository.existsByTitleIgnoreCaseAndAuthorIgnoreCaseAndPublisherIgnoreCaseAndPublicationYearAndEditionAndIsbnIgnoreCase(
                "Test Book","Test Author", "Test publisher",2010,2,"1FB3KRB3BKETBKB"))
                .thenReturn(true);
        DuplicateBookException exception = assertThrows(DuplicateBookException.class,
                ()->bookService.createBook(request));
        assertEquals("This book already exists",exception.getMessage());
        verify(bookRepository,never()).save(any(Book.class));
    }

    @Test
    void createBook_shouldSaveBook_whenBookIsNotDuplicate(){
        CreateBookRequest request = new CreateBookRequest();
        request.setTitle("Test Book");
        request.setAuthor("Test Author");
        request.setPublisher("Test publisher");
        request.setPublicationYear(2010);
        request.setEdition(2);
        request.setIsbn("1FB3KRB3BKETBKB");
        when(bookRepository.existsByTitleIgnoreCaseAndAuthorIgnoreCaseAndPublisherIgnoreCaseAndPublicationYearAndEditionAndIsbnIgnoreCase(
                "Test Book","Test Author", "Test publisher",2010,2,"1FB3KRB3BKETBKB"))
                .thenReturn(false);
        when(bookRepository.save(any(Book.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        BookResponse resultBook = bookService.createBook(request);
        assertEquals("Test Book", resultBook.title());
        assertEquals("Test Author", resultBook.author());
        assertEquals("Test publisher", resultBook.publisher());
        assertEquals(2010, resultBook.publicationYear());
        assertEquals(2, resultBook.edition());
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void updateBook_shouldUpdateBook_whenRequestIsValid(){
        Book book = new Book("Test Book","Test Author", "Test publisher",2010,2,"1FB3KRB3BKETBKB");
        when(bookRepository.findById(1L))
                .thenReturn(Optional.of(book));
        when(bookRepository.save(book))
                .thenAnswer(invocation -> invocation.getArgument(0));
        UpdateBookRequest request = new UpdateBookRequest();
        request.setTitle("Test Book");
        request.setAuthor("Test Author");
        request.setPublisher("Test publisher");
        request.setPublicationYear(2020);
        request.setEdition(3);
        request.setIsbn("1FB3KRB3BKETBKB");
        BookResponse resultBook = bookService.updateBook(1L,request);
        assertEquals(2020, resultBook.publicationYear());
        assertEquals(3, resultBook.edition());
        verify(bookRepository).save(book);
    }

    @Test
    void updateBook_shouldFail_whenBookDoesNotExist(){
        when(bookRepository.findById(1L))
                .thenReturn(Optional.empty());
        UpdateBookRequest request = new UpdateBookRequest();
        request.setTitle("Test Book");
        request.setAuthor("Test Author");
        request.setPublisher("Test publisher");
        request.setPublicationYear(2020);
        request.setEdition(3);
        BookNotFoundException exception = assertThrows(BookNotFoundException.class,
                ()->bookService.updateBook(1L,request));
        assertEquals("Book not found with ID: 1",exception.getMessage());
        verify(bookRepository,never()).save(any(Book.class));
    }

}
