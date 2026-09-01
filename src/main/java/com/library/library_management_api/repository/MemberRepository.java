package com.library.library_management_api.repository;

import com.library.library_management_api.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member,Long> {

    boolean existsByEmailIgnoreCase(String email);
}
