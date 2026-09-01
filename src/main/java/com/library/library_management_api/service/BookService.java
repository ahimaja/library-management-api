package com.library.library_management_api.service;

import com.library.library_management_api.dto.BookResponse;
import com.library.library_management_api.dto.CreateBookRequest;
import com.library.library_management_api.dto.UpdateBookRequest;
import com.library.library_management_api.exception.BookNotFoundException;
import com.library.library_management_api.exception.DuplicateBookException;
import com.library.library_management_api.model.Book;
import com.library.library_management_api.repository.BookRecordRepository;
import com.library.library_management_api.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookRecordRepository bookRecordRepository;

    public BookService(BookRepository bookRepository,BookRecordRepository bookRecordRepository){
        this.bookRepository=bookRepository;
        this.bookRecordRepository=bookRecordRepository;
    }

    public BookResponse createBook(CreateBookRequest request){
        boolean duplicateExists = bookRepository.existsByTitleIgnoreCaseAndAuthorIgnoreCaseAndPublisherIgnoreCaseAndPublicationYearAndEdition(
                request.getTitle(),request.getAuthor(),request.getPublisher(),request.getPublicationYear(),request.getEdition());
        if(duplicateExists)
            throw new DuplicateBookException("This book already exists");
        Book book=new Book(request.getTitle(),request.getAuthor(),request.getPublisher(),request.getPublicationYear(),request.getEdition());
        Book savedBook = bookRepository.save(book);
        return toResponse(savedBook);
    }

    private BookResponse toResponse(Book book){
        return new BookResponse(
                book.getBookId(),
                book.getTitle(),
                book.getAuthor(),
                book.getPublisher(),
                book.getPublicationYear(),
                book.getEdition()
        );
    }

    public List<BookResponse> getAllBooks(){
        return bookRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public Book getBookById(Long bookId){
        return bookRepository.findById(bookId)
                .orElseThrow(()->new BookNotFoundException("Book not found with ID: "+bookId));
    }

    public BookResponse getBookResponseById(Long bookId){
        return toResponse(getBookById(bookId));
    }

    public BookResponse updateBook(Long bookId, UpdateBookRequest request){
        Book existingBook = getBookById(bookId);
        existingBook.updateDetails(request.getTitle(),
                request.getAuthor(),
                request.getPublisher(),
                request.getPublicationYear(),
                request.getEdition());

        Book updatedBook = bookRepository.save(existingBook);
        return toResponse(updatedBook);
    }

    public void deleteBook(Long bookId){
        bookRepository.findById(bookId)
                .orElseThrow(()->new BookNotFoundException("Book not found with ID: "+bookId));
        if(bookRecordRepository.existsByBookBookId(bookId))
            throw new IllegalStateException("Book cannot be deleted while book records exist");
        bookRepository.deleteById(bookId);
    }

    public List<BookResponse> searchBooks(String keyword){
        return bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrPublisherContainingIgnoreCase
                (keyword,keyword,keyword).stream()
                .map(this::toResponse)
                .toList();
    }
}
