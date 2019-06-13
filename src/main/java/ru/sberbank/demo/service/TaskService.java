package ru.sberbank.demo.service;

import lombok.SneakyThrows;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.sberbank.demo.model.Document;
import ru.sberbank.demo.model.request.DocumentsRequest;
import ru.sberbank.demo.model.request.TransferRequest;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Сервис переводов и документов
 */
@Service
public class TaskService {
    private TransferService transferService;
    private UserService userService;

    @Autowired
    public TaskService(TransferService transferService, UserService userService) {
        this.transferService = transferService;
        this.userService = userService;
    }

    // Процедура совершения проводки
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

    // Процедура получения списка документов по пользователю за период дат
    @Async("taskExecutor")
    public CompletableFuture<List<Document>> getDocuments(DocumentsRequest request, Long userId) {
        val user = userService.login(userId, request.getPassword());
        if(user != null) {
            val documents = transferService.getDocuments(request.getStart(), request.getEnd(), user);
            return CompletableFuture.completedFuture(documents);
        } else {
            return null;
        }
    }
}
