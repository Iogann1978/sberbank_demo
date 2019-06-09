package ru.sberbank.demo.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@Builder
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotNull
    private User user;
    @NotNull
    private Account from;
    @NotNull
    private Account to;
    @NotNull
    private BigDecimal sum;
    @CreationTimestamp
    private Date timestamp;
    @NotNull
    private String text;
}
