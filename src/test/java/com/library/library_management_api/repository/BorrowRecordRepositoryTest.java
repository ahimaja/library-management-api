package com.library.library_management_api.repository;

import com.library.library_management_api.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BorrowRecordRepositoryTest {

    @Autowired
    private BorrowRecordRepository borrowRecordRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BookRecordRepository bookRecordRepository;

    private Book book;
    private Member member;

    private final LocalDate TEST_DATE = LocalDate.of(2026,8,11);

    @BeforeEach
    void setUp() {
        book = new Book("Test Title", "Test Author", "Test Publisher", 2010, 2);
        bookRepository.save(book);
        member = new Member("Hima", "hima@gmail.com", "9191919191");
        memberRepository.save(member);
    }

    @Test
    void findByStatusAndDueDateBefore_shouldOnlyReturnActiveOverdueRecords(){
        BookRecord bookRecord1 = bookRecordRepository.save(new BookRecord(book));
        BookRecord bookRecord2 = bookRecordRepository.save(new BookRecord(book));
        BookRecord bookRecord3 = bookRecordRepository.save(new BookRecord(book));

        BorrowRecord overdueActive = new BorrowRecord(member,bookRecord1,TEST_DATE.minusDays(20),TEST_DATE.minusDays(6));
        borrowRecordRepository.save(overdueActive);
        BorrowRecord notOverdue = new BorrowRecord(member,bookRecord2,TEST_DATE.minusDays(9),TEST_DATE.plusDays(5));
        borrowRecordRepository.save(notOverdue);
        BorrowRecord alreadyReturned = new BorrowRecord(member,bookRecord3,TEST_DATE.minusDays(20),TEST_DATE.minusDays(6));
        alreadyReturned.markAsReturned(TEST_DATE.minusDays(2));
        borrowRecordRepository.save(alreadyReturned);
        List<BorrowRecord> resultBorrowRecords = borrowRecordRepository.findByStatusAndDueDateBefore(BorrowStatus.ACTIVE,TEST_DATE);
        assertEquals(1, resultBorrowRecords.size());
        BorrowRecord resultBorrowRecord = resultBorrowRecords.getFirst();
        assertEquals(overdueActive.getBorrowId(),resultBorrowRecord.getBorrowId());
    }

}
