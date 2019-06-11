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
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class TransferService {
    private DocumentRepository documentRepository;
    private AccountRepository accountRepository;

    @Autowired
    public TransferService(AccountRepository accountRepository, DocumentRepository documentRepository) {
        this.accountRepository = accountRepository;
        this.documentRepository = documentRepository;
    }

    @Transactional(rollbackFor = {InsufficientFunds.class, NegativeSum.class})
    public void transfer(Account a, Account b, BigDecimal sum) throws InsufficientFunds, NegativeSum {
        val doc = Document.builder()
                .from(a)
                .to(b)
                .sum(sum)
                .user(a.getUser())
                .build();
        a.transferFrom(sum);
        b.transferTo(sum);
        accountRepository.save(a);
        accountRepository.save(b);
        documentRepository.save(doc);
    }

    @Transactional
    public List<Document> getDocuments(Date start, Date end, User user) {
        return documentRepository.getDocumentsBetween(start, end, user);
    }
}
