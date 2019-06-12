package ru.sberbank.demo;

import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import ru.sberbank.demo.model.Account;
import ru.sberbank.demo.model.AccountType;
import ru.sberbank.demo.model.Document;
import ru.sberbank.demo.model.request.AccountRequest;
import ru.sberbank.demo.model.request.DocumentsRequest;
import ru.sberbank.demo.model.request.TransferRequest;
import ru.sberbank.demo.model.User;
import ru.sberbank.demo.model.request.UserRequest;
import ru.sberbank.demo.repository.AccountRepository;
import ru.sberbank.demo.repository.UserRepository;
import ru.sberbank.demo.service.TaskService;
import ru.sberbank.demo.service.UserService;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@PropertySource("classpath:application-test.yaml")
@Slf4j
public class DemoApplicationTests {
    @LocalServerPort
    private int port;

    private static final String accNumberFrom = "40817810000000000001", accNumberTo = "40817810000000000002";
    private static final String password = "12345";
    private RestTemplate restTemplate = new RestTemplate();

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
        val userResponse = restTemplate.exchange("http://localhost:" + port + "/user/add", HttpMethod.POST,
                httpUserRequest, User.class);
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
        val accFromResponse = restTemplate.exchange("http://localhost:" + port + "/user/account/add/" + user.getId(),
                HttpMethod.POST, httpAccountFromRequest, Account.class);
        assertNotNull(accFromResponse);
        assertEquals(HttpStatus.OK, accFromResponse.getStatusCode());
        assertTrue(accFromResponse.hasBody());
        assertNotNull(accFromResponse.getBody());
        log.info("accountFrom: {}", accFromResponse.getBody());

        val accountRequestTo = AccountRequest.builder()
                .name("Credit Account User 1")
                .number(accNumberTo)
                .type(AccountType.Credit)
                .amount(BigDecimal.ZERO)
                .password(password)
                .build();
        val httpAccountToRequest = new HttpEntity<>(accountRequestTo, headers);
        val accToResponse = restTemplate.exchange("http://localhost:" + port + "/user/account/add/" + user.getId(),
                HttpMethod.POST, httpAccountToRequest, Account.class);
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
        val transferResponse = restTemplate.exchange("http://localhost:" + port + "/transfer/action/" + user.getId(),
                HttpMethod.POST, httpTransferRequest, String.class);
        assertNotNull(transferResponse);
        assertEquals(HttpStatus.OK, transferResponse.getStatusCode());

        try {
            TimeUnit.SECONDS.sleep(3L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        val httpAccountsRequest = new HttpEntity<>(password, headers);
        val accountsResponse = restTemplate.exchange("http://localhost:" + port + "/user/" + user.getId(),
                HttpMethod.POST, httpAccountsRequest, User.class);
        assertNotNull(accountsResponse);
        assertEquals(HttpStatus.OK, accountsResponse.getStatusCode());
        assertTrue(accountsResponse.hasBody());
        assertNotNull(accountsResponse.getBody());
        accountsResponse.getBody().getAccounts().stream().
                forEach(account -> log.info("account: {} {}", account.getNumber(), account.getAmount()));

        val documentsRequest = DocumentsRequest.builder()
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .password(password)
                .build();
        val httpDocumentsRequest = new HttpEntity<>(documentsRequest, headers);
        val documentsResponse = restTemplate.exchange("http://localhost:" + port + "/transfer/documents/" + user.getId(),
                HttpMethod.POST, httpDocumentsRequest, new ParameterizedTypeReference<List<Document>>(){});
        assertNotNull(documentsResponse);
        assertEquals(HttpStatus.OK, documentsResponse.getStatusCode());
        assertTrue(documentsResponse.hasBody());
        assertNotNull(documentsResponse.getBody());
        val documents = documentsResponse.getBody();
        documents.stream().forEach(document -> log.info("document: {} {} {} {}",
                document.getTimestamp(), document.getFrom().getNumber(), document.getTo().getNumber(),
                document.getSum()));
    }
}
