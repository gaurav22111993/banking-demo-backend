package com.demo.bank.repo;
import com.demo.bank.model.Beneficiary;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface BeneficiaryRepository extends JpaRepository<Beneficiary,Long>{
    List<Beneficiary> findByUserId(Long userId);
}
