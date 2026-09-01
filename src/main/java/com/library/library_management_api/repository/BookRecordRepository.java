package com.library.library_management_api.repository;

import com.library.library_management_api.model.BookRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRecordRepository extends JpaRepository<BookRecord,Long> {

    List<BookRecord> findByBookBookId(Long bookId);

    boolean existsByBookBookId(Long bookId);
}
