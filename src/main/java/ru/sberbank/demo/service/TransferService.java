package ru.sberbank.demo.service;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sberbank.demo.error.InsufficientFunds;
import ru.sberbank.demo.error.NegativeSum;
import ru.sberbank.demo.model.Account;
import ru.sberbank.demo.model.Document;
import ru.sberbank.demo.repository.AccountRepository;
import ru.sberbank.demo.repository.DocumentRepository;

import javax.transaction.Transactional;
import java.math.BigDecimal;

@Service
public class TransferService {
    private DocumentRepository documentRepository;
    private AccountRepository accountRepository;

    @Autowired
    public TransferService(AccountRepository accountRepository, DocumentRepository documentRepository) {
        this.accountRepository = accountRepository;
        this.documentRepository = documentRepository;
    }

    @Transactional(rollbackOn = {InsufficientFunds.class, NegativeSum.class})
    public void transfer(Account a, Account b, BigDecimal sum) throws InsufficientFunds, NegativeSum {
        val doc = Document.builder()
                .from(a)
                .to(b)
                .sum(sum)
                .build();
        a.transferFrom(sum);
        b.transferTo(sum);
        accountRepository.save(a);
        accountRepository.save(b);
        documentRepository.save(doc);
    }
}