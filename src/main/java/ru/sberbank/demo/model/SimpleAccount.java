package ru.sberbank.demo.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sberbank.demo.error.InsufficientFunds;
import ru.sberbank.demo.error.NegativeSum;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
public class SimpleAccount implements Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Size(min = 20, max = 20)
    private String number;
    private String name;
    @Positive
    private BigDecimal amount;
    @NotNull
    private AccountType type;
    @ManyToOne
    @JoinColumn(name = "ACCOUNT_ID")
    private User user;

    @Override
    public BigDecimal transferTo(BigDecimal sum) throws NegativeSum {
        checkSum(sum);
        amount.add(sum);
        return amount;
    }

    @Override
    public BigDecimal transferFrom(BigDecimal sum) throws InsufficientFunds, NegativeSum {
        checkSum(sum);
        if(amount.compareTo(sum) == -1) {
            throw new InsufficientFunds();
        }
        amount.subtract(sum);
        return amount;
    }

    private static void checkSum(BigDecimal sum) throws NegativeSum {
        if(sum.compareTo(BigDecimal.ZERO) == 0 || sum.compareTo(BigDecimal.ZERO) == -1) {
            throw new NegativeSum();
        }
    }
}