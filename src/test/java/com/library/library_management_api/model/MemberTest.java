package com.library.library_management_api.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MemberTest {

    private Member member;
    @BeforeEach
    void setUp(){
        member = new Member("Hima","hima@gmail.com","9191919191");
    }

    @Test
    void addFine_shouldIncreaseOutstandingFine(){
        member.addFine(15L);
        assertEquals(15L,member.getOutstandingFine());
    }

    @Test
    void addFine_shouldFail_WhenAmountIsNegative(){
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                ()->member.addFine(-15L));
        assertEquals("Fine amount cannot be negative",exception.getMessage());
        assertEquals(0L,member.getOutstandingFine());
    }

    @Test
    void recordFinePayment_shouldReduceOutstandingFine(){
        member.addFine(20L);
        member.recordFinePayment(10L);
        assertEquals(10L,member.getOutstandingFine());
    }

    @Test
    void recordFinePayment_shouldFail_whenPaymentAmountIsNotPositive(){
        member.addFine(10L);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                ()->member.recordFinePayment(-10L));
        assertEquals("Payment amount must be greater than zero",exception.getMessage());
        assertEquals(10L,member.getOutstandingFine());
    }

    @Test
    void recordFinePayment_shouldFail_whenPaymentAmountExceedsOutstandingAmount(){
        member.addFine(20L);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                ()->member.recordFinePayment(25L));
        assertEquals("Payment cannot exceed outstanding amount",exception.getMessage());
        assertEquals(20L,member.getOutstandingFine());
    }
}
