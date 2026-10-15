package com.library.library_management_api.repository;

import com.library.library_management_api.model.BorrowRecord;
import com.library.library_management_api.model.BorrowStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord,Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT br from BorrowRecord br WHERE br.borrowId= :id")
    Optional<BorrowRecord> findByIdForUpdate(@Param("id") Long borrowId);



    long countByMemberMemberIdAndStatus(Long memberId,BorrowStatus status);

    List<BorrowRecord> findByMemberMemberIdAndStatus(Long memberId, BorrowStatus status);

    List<BorrowRecord> findByMemberMemberId(Long memberId);

    List<BorrowRecord> findByStatusAndDueDateBefore(BorrowStatus status, LocalDate currentDate);
}
