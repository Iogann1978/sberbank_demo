package ru.sberbank.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.sberbank.demo.model.TransferRequest;
import ru.sberbank.demo.service.TaskService;

@RestController
@RequestMapping("transfer")
public class TransferController {
    private TaskService taskService;

    @Autowired
    public TransferController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/simple/{userId}")
    public void transfer(@RequestBody TransferRequest request, @PathVariable Long userId) {
        taskService.transferTask(request, userId);
    }
}
