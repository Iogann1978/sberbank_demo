package ru.sberbank.demo;

import lombok.val;
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
import ru.sberbank.demo.model.User;
import ru.sberbank.demo.repository.UserRepository;

import java.math.BigDecimal;
import java.util.HashSet;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@PropertySource("classpath:application-test.yaml")
public class DemoApplicationTests {
    @Autowired
    private UserRepository userRepository;

    private Long userId;

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
        userId = userRepository.save(user).getId();
    }

    @Test
    public void testUser() {
        val user = userRepository.findById(userId);
        assertTrue(user.isPresent());
    }

}
