package com.library.library_management_api.security.controller;

import com.library.library_management_api.security.dto.CreateStaffRequest;
import com.library.library_management_api.security.dto.UpdateStaffRoleRequest;
import com.library.library_management_api.security.dto.UserAccountResponse;
import com.library.library_management_api.security.service.UserAccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/staff")
public class AdminStaffController {

    private UserAccountService userAccountService;

    public AdminStaffController(UserAccountService userAccountService){
        this.userAccountService=userAccountService;
    }

    @PostMapping
    public ResponseEntity<UserAccountResponse> createStaffAccount(@Valid @RequestBody CreateStaffRequest request){
        UserAccountResponse response = userAccountService.createStaffAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/enable")
    public ResponseEntity<Void> enableStaffAccount(@PathVariable("id") Long userId){
        userAccountService.enableStaffAccount(userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/disable")
    public ResponseEntity<Void> disableStaffAccount(@PathVariable("id") Long userId){
        userAccountService.disableStaffAccount(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<UserAccountResponse>> getAllStaffAccounts(){
        return ResponseEntity.ok(userAccountService.getAllStaffAccounts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserAccountResponse> getStaffAccountById(@PathVariable("id") Long userId){
        return ResponseEntity.ok(userAccountService.getStaffAccountById(userId));
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<Void> updateStaffRole(@PathVariable("id") Long userId,
                                                @Valid @RequestBody UpdateStaffRoleRequest request){
        userAccountService.updateStaffRole(userId,request.role());
        return ResponseEntity.noContent().build();
    }

}
