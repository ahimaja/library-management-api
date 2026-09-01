package com.library.library_management_api.security.service;

import com.library.library_management_api.security.model.UserAccount;
import com.library.library_management_api.security.repository.UserAccountRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;

    public CustomUserDetailsService(UserAccountRepository userAccountRepository){
        this.userAccountRepository=userAccountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserAccount userAccount = userAccountRepository.findByEmailIgnoreCase(email)
                .orElseThrow(()-> new UsernameNotFoundException("User not found"));
        UserDetails userDetails = User
                .withUsername(userAccount.getEmail())
                .password(userAccount.getPassword())
                .roles(userAccount.getRole().name())
                .disabled(!userAccount.isEnabled())
                .build();
        return userDetails;
    }
}
