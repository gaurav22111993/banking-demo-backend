package com.demo.bank.repo;
import com.demo.bank.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface AccountRepository extends JpaRepository<Account,Long>{
    Optional<Account> findByAccountNumber(String number);
    Optional<Account> findByUserId(Long userId);
}
