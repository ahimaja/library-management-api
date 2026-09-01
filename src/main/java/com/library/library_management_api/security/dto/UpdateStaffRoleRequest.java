package com.library.library_management_api.security.dto;

import com.library.library_management_api.security.model.Role;
import jakarta.validation.constraints.NotNull;

public record UpdateStaffRoleRequest(@NotNull(message = "Role name is required")Role role) {
}
