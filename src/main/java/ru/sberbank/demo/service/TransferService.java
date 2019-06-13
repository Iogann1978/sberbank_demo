package ru.sberbank.demo.service;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.demo.error.InsufficientFunds;
import ru.sberbank.demo.error.NegativeSum;
import ru.sberbank.demo.model.Account;
import ru.sberbank.demo.model.Document;
import ru.sberbank.demo.model.User;
import ru.sberbank.demo.repository.AccountRepository;
import ru.sberbank.demo.repository.DocumentRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class TransferService {
    private DocumentRepository documentRepository;
    private AccountRepository accountRepository;

    @Autowired
    public TransferService(AccountRepository accountRepository,
                           DocumentRepository documentRepository) {
        this.accountRepository = accountRepository;
        this.documentRepository = documentRepository;
    }

    // Транзакция проводки
    @Transactional(rollbackFor = {InsufficientFunds.class, NegativeSum.class})
    public void transfer(User user, Account a, Account b, BigDecimal sum) throws InsufficientFunds, NegativeSum {
        val doc = Document.builder()
                .from(a)
                .to(b)
                .sum(sum)
                .user(user)
                .build();
        a.transferFrom(sum);
        b.transferTo(sum);
        accountRepository.save(a);
        accountRepository.save(b);
        documentRepository.save(doc);
    }

    // Транзакция выборки документов
    @Transactional(readOnly = true)
    public List<Document> getDocuments(LocalDateTime start, LocalDateTime end, User user) {
        return documentRepository.getDocumentsBetween(Date.from(start.atZone(ZoneId.systemDefault()).toInstant()),
                Date.from(end.atZone(ZoneId.systemDefault()).toInstant()), user);
    }
}
