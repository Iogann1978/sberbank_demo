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
import org.springframework.core.annotation.Order;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
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
    private static final String accNumberFrom = "40817810000000000001", accNumberTo = "40817810000000000002";

    @Before
    public void setUp() {

    }

    @Test
    @Order(1)
    public void testUser() {
        val accounts = new HashSet<Account>(){
            {
                add(Account.builder()
                        .name("First account")
                        .type(AccountType.Current)
                        .number(accNumberFrom)
                        .amount(new BigDecimal(1000.0))
                        .build());
                add(Account.builder()
                        .name("Second account")
                        .type(AccountType.Current)
                        .number(accNumberTo)
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
        accountRepository.saveAll(accounts);

        assertNotNull(testUser);
        assertNotNull(testUser.getId());
        val request = TransferRequest.builder()
                .form(accNumberFrom)
                .to(accNumberTo)
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

    }

    @Test
    @Order(2)
    @Transactional
    public void print() {
        val testAccountFrom = accountRepository.findAccountByNumber(accNumberFrom);
        val testAccountTo = accountRepository.findAccountByNumber(accNumberTo);
        assertTrue(testAccountFrom.isPresent());
        assertTrue(testAccountTo.isPresent());
        log.info("from: {} {}", testAccountFrom.get().getId(), testAccountFrom.get().getAmount());
        log.info("to: {} {}", testAccountTo.get().getId(), testAccountTo.get().getAmount());
    }
}
