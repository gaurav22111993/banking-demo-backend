package com.demo.bank.controller;

import com.demo.bank.model.Beneficiary;
import com.demo.bank.service.BankService;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.*;

@RestController @RequestMapping("/api")
public class BankController {
    private final BankService bank;
    public BankController(BankService bank){this.bank=bank;}

    public record Money(BigDecimal amount,String description){}
    public record Transfer(String toAccount,BigDecimal amount,String description){}
    public record BeneficiaryRequest(String name,String accountNumber){}

    @GetMapping("/dashboard") public Map<String,Object> dashboard(Principal p){return bank.dashboard(p.getName());}

    @PostMapping("/deposit") public Map<String,String> deposit(Principal p,@RequestBody Money r){
        bank.money(p.getName(),r.amount(),true,r.description()==null?"Demo deposit":r.description());
        return Map.of("message","Deposit successful");
    }

    @PostMapping("/withdraw") public Map<String,String> withdraw(Principal p,@RequestBody Money r){
        bank.money(p.getName(),r.amount(),false,r.description()==null?"Demo withdrawal":r.description());
        return Map.of("message","Withdrawal successful");
    }

    @PostMapping("/transfer") public Map<String,String> transfer(Principal p,@RequestBody Transfer r){
        bank.transfer(p.getName(),r.toAccount(),r.amount(),r.description()==null?"Demo transfer":r.description());
        return Map.of("message","Transfer successful");
    }

    @GetMapping("/beneficiaries") public List<Beneficiary> beneficiaries(Principal p){return bank.beneficiaries(p.getName());}

    @PostMapping("/beneficiaries") public Beneficiary add(Principal p,@RequestBody BeneficiaryRequest r){
        return bank.addBeneficiary(p.getName(),r.name(),r.accountNumber());
    }

    @DeleteMapping("/beneficiaries/{id}") public Map<String,String> delete(Principal p,@PathVariable Long id){
        bank.deleteBeneficiary(p.getName(),id); return Map.of("message","Deleted");
    }
}
