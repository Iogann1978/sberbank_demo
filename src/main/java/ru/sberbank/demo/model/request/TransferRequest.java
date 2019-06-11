package ru.sberbank.demo.model.request;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TransferRequest {
    private String password;
    private String form;
    private String to;
    private BigDecimal sum;
}
