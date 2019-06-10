package ru.sberbank.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import ru.sberbank.demo.error.LoginException;
import ru.sberbank.demo.model.Account;
import ru.sberbank.demo.model.User;
import ru.sberbank.demo.repository.UserRepository;

import java.util.Optional;

@Service
@Slf4j
public class UserService {
    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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

    @Cacheable("accounts")
    public Optional<Account> getAccount(User user, String number) {
        return user.getAccounts().stream().filter(account -> account.getNumber().equals(number)).findFirst();
    }
}
