package ru.sberbank.demo.config;

import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.concurrent.ForkJoinPool;

@Configuration
@EnableAsync
public class TaskConfig {
    @Bean(name="taskExecutor")
    public TaskExecutor htmlExecutor() {
        val taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setThreadNamePrefix("task-");
        taskExecutor.setCorePoolSize(10);
        taskExecutor.setMaxPoolSize(10);
        taskExecutor.setQueueCapacity(10);
        taskExecutor.initialize();
        return taskExecutor;
    }
}
