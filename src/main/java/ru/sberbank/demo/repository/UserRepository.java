package ru.sberbank.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sberbank.demo.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
