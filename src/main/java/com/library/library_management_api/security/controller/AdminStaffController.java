package com.library.library_management_api.security.controller;

import com.library.library_management_api.security.dto.CreateStaffRequest;
import com.library.library_management_api.security.dto.UpdateStaffRoleRequest;
import com.library.library_management_api.security.dto.UserAccountResponse;
import com.library.library_management_api.security.service.UserAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name="Staff administration", description = "Operations for managing employee and administrator accounts")
@SecurityRequirement(name = "bearerAuth")
@ApiResponses({
        @ApiResponse(responseCode = "401", description = "Authentication required or JWT is invalid")
})
@RestController
@RequestMapping("/admin/staff")
public class AdminStaffController {

    private UserAccountService userAccountService;

    public AdminStaffController(UserAccountService userAccountService){
        this.userAccountService=userAccountService;
    }



    @Operation(summary = "Create a staff account",
            description = "Creates a user account with EMPLOYEE/ADMIN role. Requires ADMIN role")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Staff account created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid staff account creation data"),
            @ApiResponse(responseCode = "403", description = "Access denied. Only user with ADMIN role can access this end point"),
            @ApiResponse(responseCode = "409", description = "Email is already registered with another staff account  or the requested role is not permitted")
    })
    @PostMapping
    public ResponseEntity<UserAccountResponse> createStaffAccount(@Valid @RequestBody CreateStaffRequest request){
        UserAccountResponse response = userAccountService.createStaffAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }



    @Operation(summary = "Enable the staff account",
            description = "Enables an existing EMPLOYEE/ADMIN account. Requires ADMIN role")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Staff account enabled successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied. Only user with ADMIN role can access this end point"),
            @ApiResponse(responseCode = "409", description = "User account not found or the account does not belong to staff ")
    })
    @PatchMapping("/{id}/enable")
    public ResponseEntity<Void> enableStaffAccount(@PathVariable("id") Long userId){
        userAccountService.enableStaffAccount(userId);
        return ResponseEntity.noContent().build();
    }




    @Operation(summary = "Disable the staff account",
            description = "Diables a user account with EMPLOYEE/ADMIN role while preventing the last enabled administrator from being disabled. " +
                    "Requires ADMIN role")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Staff account disabled successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied. Only user with ADMIN role can access this end point"),
            @ApiResponse(responseCode = "409", description = "User account not found or the account does not belong to staff or the account belongs to last enabled ADMIN")
    })
    @PatchMapping("/{id}/disable")
    public ResponseEntity<Void> disableStaffAccount(@PathVariable("id") Long userId){
        userAccountService.disableStaffAccount(userId);
        return ResponseEntity.noContent().build();
    }




    @Operation(summary = "Get all staff accounts", description = "Retrieves all the staff accounts. Requires ADMIN/EMPLOYEE role")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Staff accounts retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied. Only user with ADMIN role can access this end point")
    })
    @GetMapping
    public ResponseEntity<List<UserAccountResponse>> getAllStaffAccounts(){
        return ResponseEntity.ok(userAccountService.getAllStaffAccounts());
    }




    @Operation(summary = "Get a staff account", description = "Retrieves the staff account by specifying the staff id. Requires ADMIN/EMPLOYEE role")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Staff account retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied. Only user with ADMIN role can access this end point")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserAccountResponse> getStaffAccountById(@PathVariable("id") Long userId){
        return ResponseEntity.ok(userAccountService.getStaffAccountById(userId));
    }



    @Operation(summary = "Update a staff account's role",
            description = "Changes a staff account's role between EMPLOYEE and ADMIN while preventing demotion of the last enabled administrator. Requires ADMIN role")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Staff role updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid staff role update request"),
            @ApiResponse(responseCode = "403", description = "Access denied. Only user with ADMIN role can access this end point"),
            @ApiResponse(responseCode = "409", description = "User account not found or account is not staff or requested role is not permitted or the last enabled administrator cannot be demoted")
    })
    @PatchMapping("/{id}/role")
    public ResponseEntity<Void> updateStaffRole(@PathVariable("id") Long userId,
                                                @Valid @RequestBody UpdateStaffRoleRequest request){
        userAccountService.updateStaffRole(userId,request.role());
        return ResponseEntity.noContent().build();
    }

}
