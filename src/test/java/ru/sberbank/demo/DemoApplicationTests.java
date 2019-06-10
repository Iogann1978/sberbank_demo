package ru.sberbank.demo;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.hibernate.Hibernate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.DigestUtils;
import ru.sberbank.demo.model.Account;
import ru.sberbank.demo.model.AccountType;
import ru.sberbank.demo.model.TransferRequest;
import ru.sberbank.demo.model.User;
import ru.sberbank.demo.repository.AccountRepository;
import ru.sberbank.demo.repository.UserRepository;
import ru.sberbank.demo.service.TaskService;
import ru.sberbank.demo.service.UserService;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@PropertySource("classpath:application-test.yaml")
@Slf4j
public class DemoApplicationTests {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private TaskService taskService;

    private User testUser;
    private Account testAccountFrom, testAccountTo;

    @Before
    public void setUp() {
        val accounts = new HashSet<Account>(){
            {
                add(Account.builder()
                        .name("First account")
                        .type(AccountType.Current)
                        .number("40817810000000000001")
                        .amount(new BigDecimal(1000.0))
                        .build());
                add(Account.builder()
                        .name("Second account")
                        .type(AccountType.Current)
                        .number("40817810000000000002")
                        .amount(BigDecimal.ZERO)
                        .build());
            }
        };
        val user = User.builder()
                .firstName("User 1")
                .accounts(accounts)
                .password(DigestUtils.md5DigestAsHex("12345".getBytes()))
                .build();
        testUser = userRepository.save(user);
        accounts.stream().forEach(account -> account.setUser(testUser));
        accountRepository.saveAll(accounts).stream().forEach(account -> {
            if(account.getAmount().equals(BigDecimal.ZERO)) {
                testAccountTo = account;
            } else {
                testAccountFrom = account;
            }
        });
    }

    @Test
    public void testUser() {
        assertNotNull(testUser);
        assertNotNull(testUser.getId());
        assertNotNull(testAccountFrom);
        assertNotNull(testAccountFrom.getNumber());
        assertNotNull(testAccountTo);
        assertNotNull(testAccountTo.getNumber());
        val request = TransferRequest.builder()
                .form(testAccountFrom.getNumber())
                .to(testAccountTo.getNumber())
                .sum(new BigDecimal(500.0))
                .password("12345")
                .build();
        taskService.transferTask(request, testUser.getId());
        //taskService.transferTask(request, testUser.getId());
        //taskService.transferTask(request, testUser.getId());
        try {
            TimeUnit.SECONDS.sleep(3L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        val accountFrom = accountRepository.getOne(testAccountFrom.getId());
        val accountTo = accountRepository.getOne(testAccountTo.getId());
        assertNotNull(accountFrom);
        assertNotNull(accountTo);
        log.info("from: {}", accountFrom.getAmount());
        log.info("to: {}", accountTo.getAmount());
    }
}
