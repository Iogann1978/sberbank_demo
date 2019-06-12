package ru.sberbank.demo.service;

import lombok.SneakyThrows;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.sberbank.demo.model.Account;
import ru.sberbank.demo.model.Document;
import ru.sberbank.demo.model.request.DocumentRequest;
import ru.sberbank.demo.model.request.TransferRequest;

import java.util.List;

@Service
public class TaskService {
    private TransferService transferService;
    private UserService userService;

    @Autowired
    public TaskService(TransferService transferService, UserService userService) {
        this.transferService = transferService;
        this.userService = userService;
    }

    @SneakyThrows
    @Async("taskExecutor")
    public void transferTask(TransferRequest request, Long userId) {
        val user = userService.login(userId, request.getPassword());
        if(user != null) {
            val accFrom = userService.getAccount(user, request.getForm());
            val accTo = userService.getAccount(user, request.getTo());
            if(accFrom.isPresent() && accTo.isPresent()) {
                transferService.transfer(user, accFrom.get(), accTo.get(), request.getSum());
            }
        }
    }

    @Async("taskExecutor")
    public List<Document> getDocuments(DocumentRequest request, Long userId) {
        val user = userService.login(userId, request.getPassword());
        if(user != null) {
            return transferService.getDocuments(request.getStart(), request.getEnd(), user);
        } else {
            return null;
        }
    }
}
