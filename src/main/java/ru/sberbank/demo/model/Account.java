package ru.sberbank.demo.model;

import ru.sberbank.demo.error.InsufficientFunds;
import ru.sberbank.demo.error.NegativeSum;

import java.math.BigDecimal;

public interface Account {
    Long getId();
    void setId(Long id);
    BigDecimal getAmount();
    void setAmount(BigDecimal amount);
    String getName();
    void setName(String name);
    String getNumber();
    void setNumber(String number);
    AccountType getType();
    void setType(AccountType type);
    BigDecimal transferTo(BigDecimal sum) throws NegativeSum;
    BigDecimal transferFrom(BigDecimal sum) throws InsufficientFunds, NegativeSum;
}
