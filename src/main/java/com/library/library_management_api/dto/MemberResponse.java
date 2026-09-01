package com.library.library_management_api.dto;

import com.library.library_management_api.model.MemberStatus;

public record MemberResponse(Long memberId,
                             String name,
                             String email,
                             String phone,
                             long outstandingFine,
                             MemberStatus status
) {
}
