package ru.sberbank.demo.model.request;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class DocumentRequest {
    private String password;
    private Date start, end;
}
