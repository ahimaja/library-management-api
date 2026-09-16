package com.library.library_management_api.repository;

import com.library.library_management_api.model.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;


    @Test
    void existsByBookDetails_shouldReturnTrueIgnoringCase(){
        Book book = new Book("Test Title", "Test Author", "Test Publisher", 2010, 2,"1FB3KRB3BKETBKB");
        bookRepository.save(book);
        boolean exists = bookRepository.existsByTitleIgnoreCaseAndAuthorIgnoreCaseAndPublisherIgnoreCaseAndPublicationYearAndEditionAndIsbnIgnoreCase
                ("Test Title", "Test Author", "Test Publisher", 2010, 2,"1FB3KRB3BKETBKB");
        assertTrue(exists);
    }
}
