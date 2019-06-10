package ru.sberbank.demo.model;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@Builder
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotNull
    @OneToOne
    private User user;
    @NotNull
    @OneToOne
    private Account from;
    @NotNull
    @OneToOne
    private Account to;
    @NotNull
    private BigDecimal sum;
    @CreationTimestamp
    private Date timestamp;
}
