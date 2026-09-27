package com.demo.bank.service;

import com.demo.bank.model.*;
import com.demo.bank.repo.*;
import com.demo.bank.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class BankService {
    private final UserRepository users;
    private final AccountRepository accounts;
    private final TransactionRepository transactions;
    private final BeneficiaryRepository beneficiaries;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public BankService(UserRepository users, AccountRepository accounts,
                       TransactionRepository transactions, BeneficiaryRepository beneficiaries,
                       PasswordEncoder encoder, JwtService jwt){
        this.users=users; this.accounts=accounts; this.transactions=transactions;
        this.beneficiaries=beneficiaries; this.encoder=encoder; this.jwt=jwt;
    }

    @Transactional
    public Map<String,Object> register(String email,String password,String name){
        if(users.existsByEmail(email)) throw new IllegalArgumentException("Email already registered");
        User u=new User(); u.setEmail(email.toLowerCase()); u.setPassword(encoder.encode(password)); u.setFullName(name);
        users.save(u);
        Account a=new Account(); a.setUser(u); a.setAccountNumber("10"+System.currentTimeMillis()%100000000L); a.setBalance(BigDecimal.ZERO);
        accounts.save(a);
        return login(email,password);
    }

    public Map<String,Object> login(String email,String password){
        User u=users.findByEmail(email.toLowerCase()).orElseThrow(()->new IllegalArgumentException("Invalid credentials"));
        if(!encoder.matches(password,u.getPassword())) throw new IllegalArgumentException("Invalid credentials");
        Account a=accounts.findByUserId(u.getId()).orElseThrow();
        return Map.of("token",jwt.generate(u.getEmail()),"user",Map.of("id",u.getId(),"email",u.getEmail(),"name",u.getFullName()),
                "account",Map.of("accountNumber",a.getAccountNumber(),"balance",a.getBalance()));
    }

    public User current(String email){return users.findByEmail(email).orElseThrow();}

    public Map<String,Object> dashboard(String email){
        User u=current(email); Account a=accounts.findByUserId(u.getId()).orElseThrow();
        return Map.of("user",Map.of("id",u.getId(),"email",u.getEmail(),"name",u.getFullName()),
                "account",Map.of("accountNumber",a.getAccountNumber(),"balance",a.getBalance()),
                "transactions",transactions.findTop100ByAccountIdOrderByCreatedAtDesc(a.getId()));
    }

    @Transactional
    public void money(String email, BigDecimal amount, boolean deposit, String description){
        if(amount==null || amount.compareTo(BigDecimal.ZERO)<=0) throw new IllegalArgumentException("Amount must be positive");
        Account a=accounts.findByUserId(current(email).getId()).orElseThrow();
        if(!deposit && a.getBalance().compareTo(amount)<0) throw new IllegalArgumentException("Insufficient balance");
        a.setBalance(deposit?a.getBalance().add(amount):a.getBalance().subtract(amount));
        accounts.save(a); saveTxn(a,deposit?"DEPOSIT":"WITHDRAW",amount,description);
    }

    @Transactional
    public void transfer(String email,String toAccount,BigDecimal amount,String description){
        if(amount==null || amount.compareTo(BigDecimal.ZERO)<=0) throw new IllegalArgumentException("Amount must be positive");
        Account from=accounts.findByUserId(current(email).getId()).orElseThrow();
        Account to=accounts.findByAccountNumber(toAccount).orElseThrow(()->new IllegalArgumentException("Destination account not found"));
        if(from.getId().equals(to.getId())) throw new IllegalArgumentException("Cannot transfer to the same account");
        if(from.getBalance().compareTo(amount)<0) throw new IllegalArgumentException("Insufficient balance");
        from.setBalance(from.getBalance().subtract(amount)); to.setBalance(to.getBalance().add(amount));
        accounts.save(from); accounts.save(to);
        String ref=UUID.randomUUID().toString().substring(0,8).toUpperCase();
        saveTxn(from,"TRANSFER_OUT",amount,description+" | Ref "+ref);
        saveTxn(to,"TRANSFER_IN",amount,description+" | Ref "+ref);
    }

    private void saveTxn(Account a,String type,BigDecimal amount,String desc){
        Transaction t=new Transaction(); t.setAccount(a); t.setType(type); t.setAmount(amount);
        t.setDescription(desc); t.setCreatedAt(LocalDateTime.now()); t.setReference(UUID.randomUUID().toString().substring(0,10).toUpperCase());
        transactions.save(t);
    }

    public List<Beneficiary> beneficiaries(String email){return beneficiaries.findByUserId(current(email).getId());}

    public Beneficiary addBeneficiary(String email,String name,String accountNumber){
        Beneficiary b=new Beneficiary(); b.setUser(current(email)); b.setName(name); b.setAccountNumber(accountNumber); return beneficiaries.save(b);
    }

    public void deleteBeneficiary(String email,Long id){
        Beneficiary b=beneficiaries.findById(id).orElseThrow();
        if(!b.getUser().getEmail().equals(email)) throw new IllegalArgumentException("Not allowed");
        beneficiaries.delete(b);
    }
}
