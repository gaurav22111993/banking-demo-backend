package com.demo.bank.controller;
import com.demo.bank.service.BankService;
import jakarta.validation.constraints.*;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController @RequestMapping("/api/auth")
public class AuthController {
    private final BankService bank;
    public AuthController(BankService bank){this.bank=bank;}

    public record Register(@Email String email,@Size(min=8) String password,@NotBlank String name){}
    public record Login(@Email String email,@NotBlank String password){}

    @PostMapping("/register") public Map<String,Object> register(@RequestBody Register r){
        return bank.register(r.email(),r.password(),r.name());
    }
    @PostMapping("/login") public Map<String,Object> login(@RequestBody Login r){
        return bank.login(r.email(),r.password());
    }
}
