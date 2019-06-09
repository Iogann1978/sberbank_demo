package ru.sberbank.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sberbank.demo.model.Document;

public interface DocumentRepository extends JpaRepository<Document, Long> {
}
