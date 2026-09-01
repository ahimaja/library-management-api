package com.library.library_management_api.security.repository;

import com.library.library_management_api.security.model.Role;
import com.library.library_management_api.security.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount,Long> {

    Optional<UserAccount> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByMemberMemberId(Long memberId);

    List<UserAccount> findByRoleIn(List<Role> roles);

    long countByRoleAndEnabled(Role role,boolean enabled);
}
