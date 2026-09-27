package com.demo.bank.config;

import com.demo.bank.model.*;
import com.demo.bank.repo.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.math.BigDecimal;

@Configuration
public class DataSeeder {
    @Bean CommandLineRunner seed(UserRepository users,AccountRepository accounts,PasswordEncoder encoder){
        return args -> {
            if(users.existsByEmail("demo@bank.local")) return;
            User u=new User(); u.setEmail("demo@bank.local"); u.setFullName("Demo Customer"); u.setPassword(encoder.encode("Demo@123")); users.save(u);
            Account a=new Account(); a.setUser(u); a.setAccountNumber("10000001"); a.setBalance(new BigDecimal("50000.00")); accounts.save(a);
        };
    }
}
