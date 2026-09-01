package com.library.library_management_api.dto;

import jakarta.validation.constraints.NotBlank;

public record VoidBorrowRecordRequest(
        @NotBlank(message = "Void reason cannot is required")
        String reason) {
}
