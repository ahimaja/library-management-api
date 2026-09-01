package com.library.library_management_api.dto;

import jakarta.validation.constraints.Positive;

public class PayFineRequest {

    @Positive(message = "Amount must be greater than zero")
    private long amount;

    public PayFineRequest() {
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }
}
