package ru.sberbank.demo;

import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.client.RestTemplate;
import ru.sberbank.demo.model.Account;
import ru.sberbank.demo.model.AccountType;
import ru.sberbank.demo.model.request.AccountRequest;
import ru.sberbank.demo.model.request.TransferRequest;
import ru.sberbank.demo.model.User;
import ru.sberbank.demo.model.request.UserRequest;
import ru.sberbank.demo.repository.AccountRepository;
import ru.sberbank.demo.repository.UserRepository;
import ru.sberbank.demo.service.TaskService;
import ru.sberbank.demo.service.UserService;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
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

    private static final String accNumberFrom = "40817810000000000001", accNumberTo = "40817810000000000002";
    private static final String password = "12345";
    private RestTemplate restTemplate = new RestTemplate();

    /*
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
        val testUser = userRepository.save(user);
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
     */

    @Test
    public void testRegister() {
        val userRequest = UserRequest.builder()
                .firstName("User 1")
                .password(password)
                .build();
        val headers = new HttpHeaders();
        headers.setAccept(ImmutableList.of(MediaType.APPLICATION_JSON_UTF8));
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.setAcceptCharset(ImmutableList.of(StandardCharsets.UTF_8));

        val httpUserRequest = new HttpEntity<>(userRequest, headers);
        val userResponse = restTemplate.exchange("http://localhost:8080/user/add", HttpMethod.POST, httpUserRequest, User.class);
        assertNotNull(userResponse);
        assertEquals(HttpStatus.OK, userResponse.getStatusCode());
        assertTrue(userResponse.hasBody());
        assertNotNull(userResponse.getBody());
        val user = userResponse.getBody();
        log.info("user: {}", user);

        val accountRequestFrom = AccountRequest.builder()
                .name("Debit Account User 1")
                .number(accNumberFrom)
                .type(AccountType.Debit)
                .amount(new BigDecimal(1000.0))
                .password(password)
                .build();
        val httpAccountFromRequest = new HttpEntity<>(accountRequestFrom, headers);
        val accFromResponse = restTemplate.exchange("http://localhost:8080/user/account/add/" + user.getId(), HttpMethod.POST, httpAccountFromRequest, Account.class);
        assertNotNull(accFromResponse);
        assertEquals(HttpStatus.OK, accFromResponse.getStatusCode());
        assertTrue(accFromResponse.hasBody());
        assertNotNull(accFromResponse.getBody());
        log.info("accountFrom: {}", accFromResponse.getBody());

        val accountRequestTo = AccountRequest.builder()
                .name("Credit Account User 1")
                .number(accNumberTo)
                .type(AccountType.Debit)
                .amount(BigDecimal.ZERO)
                .password(password)
                .build();
        val httpAccountToRequest = new HttpEntity<>(accountRequestTo, headers);
        val accToResponse = restTemplate.exchange("http://localhost:8080/user/account/add/" + user.getId(), HttpMethod.POST, httpAccountToRequest, Account.class);
        assertNotNull(accToResponse);
        assertEquals(HttpStatus.OK, accToResponse.getStatusCode());
        assertTrue(accToResponse.hasBody());
        assertNotNull(accToResponse.getBody());
        log.info("accountTo: {}", accToResponse.getBody());

        val transferRequest = TransferRequest.builder()
                .form(accNumberFrom)
                .to(accNumberTo)
                .sum(new BigDecimal(500.0))
                .password(password)
                .build();
        val httpTransferRequest = new HttpEntity<>(transferRequest, headers);
        val transferResponse = restTemplate.exchange("http://localhost:8080/transfer/action/" + user.getId(), HttpMethod.POST, httpTransferRequest, String.class);
        assertNotNull(transferResponse);
        assertEquals(HttpStatus.OK, transferResponse.getStatusCode());
    }
}
