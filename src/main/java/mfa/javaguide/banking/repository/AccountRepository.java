package mfa.javaguide.banking.repository;

import mfa.javaguide.banking.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
