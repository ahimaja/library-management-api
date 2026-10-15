package com.library.library_management_api.repository;

import com.library.library_management_api.model.BookRecord;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRecordRepository extends JpaRepository<BookRecord,Long> {


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT br FROM BookRecord br WHERE br.bookRecordId = :id")
    Optional<BookRecord> findByIdForUpdate(@Param("id") Long bookRecordId);

    List<BookRecord> findByBookBookId(Long bookId);

    boolean existsByBookBookId(Long bookId);
}
