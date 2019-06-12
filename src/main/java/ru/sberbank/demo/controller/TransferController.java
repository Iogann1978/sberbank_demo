package ru.sberbank.demo.controller;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.sberbank.demo.model.Account;
import ru.sberbank.demo.model.Document;
import ru.sberbank.demo.model.request.DocumentsRequest;
import ru.sberbank.demo.model.request.TransferRequest;
import ru.sberbank.demo.service.TaskService;

import java.util.List;

@RestController
@RequestMapping("/transfer")
public class TransferController {
    private TaskService taskService;

    @Autowired
    public TransferController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/action/{userId}")
    public void transfer(@RequestBody TransferRequest request, @PathVariable("userId") Long userId) {
        taskService.transferTask(request, userId);
    }

    @SneakyThrows
    @PostMapping("/documents/{userId}")
    public List<Document> getDocuments(@RequestBody DocumentsRequest request, @PathVariable("userId") Long userId) {
        return taskService.getDocuments(request, userId).get();
    }
}
