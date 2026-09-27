package com.demo.bank.model;

import jakarta.persistence.*;

@Entity
@Table(name="beneficiaries")
public class Beneficiary {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String accountNumber;
    @ManyToOne(optional=false)
    private User user;

    public Long getId(){return id;}
    public String getName(){return name;}
    public void setName(String name){this.name=name;}
    public String getAccountNumber(){return accountNumber;}
    public void setAccountNumber(String accountNumber){this.accountNumber=accountNumber;}
    public User getUser(){return user;}
    public void setUser(User user){this.user=user;}
}
