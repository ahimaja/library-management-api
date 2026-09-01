package com.library.library_management_api.repository;

import com.library.library_management_api.model.BorrowRecord;
import com.library.library_management_api.model.BorrowStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord,Long> {

    long countByMemberMemberIdAndStatus(Long memberId,BorrowStatus status);

    List<BorrowRecord> findByMemberMemberIdAndStatus(Long memberId, BorrowStatus status);

    List<BorrowRecord> findByMemberMemberId(Long memberId);

    List<BorrowRecord> findByStatusAndDueDateBefore(BorrowStatus status, LocalDate currentDate);
}
