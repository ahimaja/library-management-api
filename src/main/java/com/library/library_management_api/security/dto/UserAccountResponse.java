package com.library.library_management_api.security.dto;

import com.library.library_management_api.security.model.Role;

public class UserAccountResponse {

    private final Long userId;
    private final String email;
    private final Role role;
    private final boolean enabled;

    public UserAccountResponse(Long userId, String email, Role role, boolean enabled) {
        this.userId = userId;
        this.email = email;
        this.role = role;
        this.enabled = enabled;
    }

    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
