package ru.sberbank.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sberbank.demo.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
