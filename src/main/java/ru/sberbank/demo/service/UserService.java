package ru.sberbank.demo.service;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import ru.sberbank.demo.error.LoginException;
import ru.sberbank.demo.model.Account;
import ru.sberbank.demo.model.User;
import ru.sberbank.demo.model.request.AccountRequest;
import ru.sberbank.demo.model.request.UserRequest;
import ru.sberbank.demo.repository.AccountRepository;
import ru.sberbank.demo.repository.UserRepository;

import java.util.Optional;

/**
 * Сервис работы со счетами и пользователями
 */
@Service
@Slf4j
public class UserService {
    private UserRepository userRepository;
    private AccountRepository accountRepository;

    @Autowired
    public UserService(UserRepository userRepository, AccountRepository accountRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
    }

    // Вход пользователя по паролю
    @Cacheable("login")
    public User login(Long userId, String password) {
        try {
            return userRepository.findById(userId)
                    .filter(user -> user.getPassword().equals(DigestUtils.md5DigestAsHex(password.getBytes())))
                    .orElseThrow(() -> new LoginException());
        } catch (LoginException e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Получение счетов пользователя
    @Cacheable("accounts")
    public Optional<Account> getAccount(User user, String number) {
        return user.getAccounts().stream().filter(account -> account.getNumber().equals(number)).findFirst();
    }

    // Регистрация нового пользователя
    public User registerUser(UserRequest request) {
        val user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(DigestUtils.md5DigestAsHex(request.getPassword().getBytes()))
                .build();
        return userRepository.save(user);
    }

    // Регистрация нового счета пользователя
    public Account registerAccount(AccountRequest request, Long userId) {
        val user = login(userId, request.getPassword());
        if(user != null) {
            val account = Account.builder()
                    .name(request.getName())
                    .number(request.getNumber())
                    .amount(request.getAmount())
                    .type(request.getType())
                    .user(user)
                    .build();
            return accountRepository.save(account);
        } else {
            return null;
        }
    }

    // Получение пользователя из базы
    public User getUser(String password, Long userId) {
        return login(userId, password);
    }
}
