package com.demo.bank.repo;
import com.demo.bank.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface TransactionRepository extends JpaRepository<Transaction,Long>{
    List<Transaction> findTop100ByAccountIdOrderByCreatedAtDesc(Long accountId);
}
