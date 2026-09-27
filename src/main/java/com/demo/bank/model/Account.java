package com.demo.bank.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name="accounts")
public class Account {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false, unique=true)
    private String accountNumber;
    @Column(nullable=false, precision=19, scale=2)
    private BigDecimal balance = BigDecimal.ZERO;
    @ManyToOne(optional=false)
    private User user;

    public Long getId(){return id;}
    public String getAccountNumber(){return accountNumber;}
    public void setAccountNumber(String accountNumber){this.accountNumber=accountNumber;}
    public BigDecimal getBalance(){return balance;}
    public void setBalance(BigDecimal balance){this.balance=balance;}
    public User getUser(){return user;}
    public void setUser(User user){this.user=user;}
}
