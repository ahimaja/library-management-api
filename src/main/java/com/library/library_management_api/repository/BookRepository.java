package com.library.library_management_api.repository;

import com.library.library_management_api.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book,Long> {

    boolean existsByTitleIgnoreCaseAndAuthorIgnoreCaseAndPublisherIgnoreCaseAndPublicationYearAndEditionAndIsbnIgnoreCase
            (String title, String author, String publisher, int publicationYear, int edition,String isbn);

    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrPublisherContainingIgnoreCase
            (String title,String author, String publisher);
}
