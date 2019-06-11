package ru.sberbank.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.sberbank.demo.model.Document;
import ru.sberbank.demo.model.User;

import java.util.Date;
import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    @Query("SELECT d FROM Document d WHERE d.timestamp >= ?1 AND d.timestamp <= ?2 AND d.user = ?3")
    List<Document> getDocumentsBetween(Date start, Date end, User user);
}
