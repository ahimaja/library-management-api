package com.library.library_management_api.security.service;

import com.library.library_management_api.model.Member;
import com.library.library_management_api.security.dto.CreateStaffRequest;
import com.library.library_management_api.security.dto.RegisterMemberRequest;
import com.library.library_management_api.security.dto.UserAccountResponse;
import com.library.library_management_api.security.model.Role;
import com.library.library_management_api.security.model.UserAccount;
import com.library.library_management_api.security.repository.UserAccountRepository;
import com.library.library_management_api.service.MemberService;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserAccountService {
    private final UserAccountRepository userAccountRepository;
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    public UserAccountService(UserAccountRepository userAccountRepository,
                              MemberService memberService,
                              PasswordEncoder passwordEncoder){
        this.userAccountRepository=userAccountRepository;
        this.memberService=memberService;
        this.passwordEncoder=passwordEncoder;
    }

    public UserAccountResponse registerMember(RegisterMemberRequest request){
        if(userAccountRepository.existsByEmailIgnoreCase(request.getEmail()))
            throw new IllegalStateException("User account with this email already exists");

        if(userAccountRepository.existsByMemberMemberId(request.getMemberId())) {
             throw new IllegalStateException("User account already exists for this member");
        }
        Member member = memberService.getMemberById(request.getMemberId());

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        UserAccount userAccount = new UserAccount(
                request.getEmail(),
                encodedPassword,
                Role.MEMBER,
                member);
        UserAccount savedAccount = userAccountRepository.save(userAccount);
        return toResponse(savedAccount);
    }

    private UserAccountResponse toResponse(UserAccount userAccount){
        return new UserAccountResponse(
                userAccount.getUserId(),
                userAccount.getEmail(),
                userAccount.getRole(),
                userAccount.isEnabled());
    }

    public UserAccountResponse createStaffAccount(CreateStaffRequest request){
        if(request.role()==Role.MEMBER)
            throw new IllegalStateException("Staff account role must be EMPLOYEE or ADMIN");
        if(userAccountRepository.existsByEmailIgnoreCase(request.email()))
            throw new IllegalStateException("User account with this email already exists");
        String encodedPassword = passwordEncoder.encode(request.password());
        UserAccount userAccount = new UserAccount(
                request.email(),
                encodedPassword,
                request.role(),
                null);
        UserAccount savedAccount = userAccountRepository.save(userAccount);
        return toResponse(savedAccount);
    }

    public Long getMemberIdForUser(String email){
        UserAccount userAccount = userAccountRepository.findByEmailIgnoreCase(email)
                .orElseThrow(()->new IllegalStateException("User account not found"));

        Member member = userAccount.getMember();
        if(member==null)
            throw new IllegalStateException("No member is associated with this user account");
        return member.getMemberId();
    }

    @Transactional
    public void enableStaffAccount(Long userId){
        UserAccount userAccount = userAccountRepository.findById(userId)
                .orElseThrow(()->new IllegalStateException("User account not found"));
        if(userAccount.getRole()==Role.MEMBER)
            throw new IllegalStateException("Member accounts cannot be managed through staff administration");
        userAccount.enable();
    }

    @Transactional
    public void disableStaffAccount(Long userId) {
        UserAccount userAccount = userAccountRepository.findById(userId)
                .orElseThrow(()->new IllegalStateException("User account not found"));
        if(userAccount.getRole()==Role.MEMBER)
            throw new IllegalStateException("Member accounts cannot be managed through staff administration");
        if(userAccount.getRole()==Role.ADMIN){
            long enabledAdminCount = userAccountRepository.countByRoleAndEnabled(Role.ADMIN,true);
            if(enabledAdminCount<=1)
                throw new IllegalStateException("The last enabled admin account cannot be deleted");
        }
        userAccount.disable();
    }

    public List<UserAccountResponse> getAllStaffAccounts(){
        return userAccountRepository.findByRoleIn(List.of(Role.ADMIN,Role.EMPLOYEE))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public UserAccountResponse getStaffAccountById(Long userId) {
        UserAccount userAccount = userAccountRepository.findById(userId)
                .orElseThrow(()->new IllegalStateException("User account not found"));
        if(userAccount.getRole()==Role.MEMBER)
            throw new IllegalStateException("User account is not a staff account");
        return toResponse(userAccount);
    }

    @Transactional
    public void updateStaffRole(Long userId, Role newRole){
        UserAccount userAccount = userAccountRepository.findById(userId)
                .orElseThrow(()->new IllegalStateException("User Account not found"));
        if(userAccount.getRole()==Role.MEMBER)
            throw new IllegalStateException("Member accounts cannot be managed through staff administration");
        if(newRole==Role.MEMBER)
            throw new IllegalStateException("Staff role must be employee or admin");
        if(userAccount.getRole()==Role.ADMIN && newRole==Role.EMPLOYEE && userAccount.isEnabled()){
            long enabledAdminCount = userAccountRepository.countByRoleAndEnabled(Role.ADMIN,true);
            if(enabledAdminCount<=1)
                throw new IllegalStateException("The last enabled admin account cannot be demoted");
        }
        userAccount.changeRole(newRole);
    }

}
