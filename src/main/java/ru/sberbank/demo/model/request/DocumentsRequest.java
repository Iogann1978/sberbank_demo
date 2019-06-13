package ru.sberbank.demo.model.request;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * Класс REST-запроса для получения списка документов по пользователю
 */
@Data
@Builder
public class DocumentsRequest {
    private String password;
    private LocalDateTime start, end;
}
