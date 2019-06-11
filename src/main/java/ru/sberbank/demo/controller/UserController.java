package ru.sberbank.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.sberbank.demo.model.Account;
import ru.sberbank.demo.model.User;
import ru.sberbank.demo.model.request.AccountRequest;
import ru.sberbank.demo.model.request.UserRequest;
import ru.sberbank.demo.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/add")
    public User registerUser(@RequestBody UserRequest request) {
        return userService.registerUser(request);
    }

    @PostMapping("/account/add/{userId}")
    public Account registerAccount(@RequestBody AccountRequest request, @PathVariable("userId") Long userId) {
        return userService.registerAccount(request, userId);
    }

    @GetMapping("/accounts/{userId}")
    public List<Account> getAccounts(@RequestBody String password, @PathVariable("userId") Long userId) {
        return userService.getAccounts(password, userId);
    }
}