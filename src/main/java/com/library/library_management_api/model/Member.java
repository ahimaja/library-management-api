package com.library.library_management_api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "members")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    private String name;
    private String email;
    private String phone;

    private long outstandingFine;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status;

    protected Member() {
    }

    public Member(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.outstandingFine = 0;
        this.status=MemberStatus.ACTIVE;
    }

    public Long getMemberId() {
        return memberId;
    }


    public String getName() {
        return name;
    }


    public String getEmail() {
        return email;
    }


    public String getPhone() {
        return phone;
    }

    public long getOutstandingFine() {
        return outstandingFine;
    }

    public void updateDetails(String name,String email, String phone){
        this.name=name;
        this.email=email;
        this.phone=phone;
    }

    public MemberStatus getStatus(){
        return status;
    }

    public void deactivate(){
        if(status==MemberStatus.INACTIVE)
            throw new IllegalStateException("Member is already inactive");
        status=MemberStatus.INACTIVE;
    }

    public void activate(){
        if(status==MemberStatus.ACTIVE)
            throw new IllegalStateException("Member is already active");
        status=MemberStatus.ACTIVE;
    }

    public void addFine(long fineAmount){
        if(fineAmount<0)
            throw new IllegalArgumentException("Fine amount cannot be negative");
        outstandingFine+=fineAmount;
    }

    public void recordFinePayment(long paymentAmount){
        if(paymentAmount<0)
            throw new IllegalArgumentException("Payment amount must be greater than zero");
        if(paymentAmount>outstandingFine)
            throw new IllegalArgumentException("Payment cannot exceed outstanding amount");
        outstandingFine-=paymentAmount;
    }

    @Override
    public String toString() {
        return "Member{" +
                "memberId=" + memberId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", outstandingFine=" + outstandingFine +
                '}';
    }
}
