package ru.sberbank.demo.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import ru.sberbank.demo.error.InsufficientFunds;
import ru.sberbank.demo.error.NegativeSum;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Size(min = 20, max = 20)
    private String number;
    private String name;
    @PositiveOrZero
    private BigDecimal amount;
    @NotNull
    private AccountType type;
    @ManyToOne
    @JoinColumn(name = "ACCOUNT_ID")
    @EqualsAndHashCode.Exclude
    private User user;

    public synchronized void transferTo(BigDecimal sum) throws NegativeSum {
        checkSum(sum);
        log.info("add amount: {}, sum: {}", amount, sum);
        amount = amount.add(sum);
        log.info("add amount: {}", amount);
    }

    public synchronized void transferFrom(BigDecimal sum) throws InsufficientFunds, NegativeSum {
        checkSum(sum);
        log.info("subtract amount: {}, sum: {}", amount, sum);
        if(amount.compareTo(sum) == -1) {
            throw new InsufficientFunds();
        }
        amount = amount.subtract(sum);
        log.info("subtract amount: {}", amount);
    }

    private static void checkSum(BigDecimal sum) throws NegativeSum {
        if(sum.compareTo(BigDecimal.ZERO) == 0 || sum.compareTo(BigDecimal.ZERO) == -1) {
            throw new NegativeSum();
        }
    }
}