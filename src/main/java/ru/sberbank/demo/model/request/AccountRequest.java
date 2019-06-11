package ru.sberbank.demo.model.request;

import lombok.Builder;
import lombok.Data;
import ru.sberbank.demo.model.AccountType;

import java.math.BigDecimal;

@Data
@Builder
public class AccountRequest {
    private String name;
    private String number;
    private BigDecimal amount;
    private AccountType type;
    private String password;
}
