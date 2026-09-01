package com.library.library_management_api.security.model;

import com.library.library_management_api.model.Member;
import jakarta.persistence.*;

@Entity
@Table(name = "user_accounts")
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false,unique = true)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean enabled;

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

    protected UserAccount(){

    }

    public UserAccount(String email, String password, Role role, Member member){
        this.email=email;
        this.password=password;
        this.role=role;
        this.member=member;
        this.enabled=true;
    }

    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Member getMember() {
        return member;
    }

    public void enable(){
        enabled=true;
    }

    public void disable(){
        enabled=false;
    }

    public void changeRole(Role role){
        this.role=role;
    }
}
