package com.nttdata.banking.loan.model;

import org.springframework.data.annotation.Id;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Class Movement.
 * Loan microservice class Movement.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Movement {

    @Id
    private String idMovement;
    private String accountNumber;
    private Integer numberMovement;
    private String movementType;
    private Double amount;
    private Double balance;
    private String currency;
    private LocalDateTime movementDate;
    private Credit credit;
    private String idBankAccount;
    private Loan loan;

}