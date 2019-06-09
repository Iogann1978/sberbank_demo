package ru.sberbank.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sberbank.demo.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
