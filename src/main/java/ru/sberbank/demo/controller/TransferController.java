package ru.sberbank.demo.controller;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.sberbank.demo.model.TransferRequest;
import ru.sberbank.demo.service.TransferService;
import ru.sberbank.demo.service.UserService;

@RestController
@RequestMapping("transfer")
@Slf4j
public class TransferController {
    private UserService userService;
    private TransferService transferService;

    @Autowired
    public TransferController(UserService userService, TransferService transferService) {
        this.userService = userService;
        this.transferService = transferService;
    }

    @SneakyThrows
    @PostMapping("/simple/{userId}")
    public void transfer(@RequestBody TransferRequest request, @PathVariable Long userId) {
        val user = userService.login(userId, request.getPassword());
        if( user != null) {
            val accFrom = userService.getAccount(user, request.getForm());
            val accTo = userService.getAccount(user, request.getTo());
            if(accFrom.isPresent() && accTo.isPresent()) {
                transferService.transfer(accFrom.get(), accTo.get(), request.getSum());
            }
        }
    }
}
