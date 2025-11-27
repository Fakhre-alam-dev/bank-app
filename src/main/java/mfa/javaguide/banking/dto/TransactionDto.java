package mfa.javaguide.banking.dto;

import java.time.LocalDateTime;

public record TransactionDto
        (
        Long id,
        Long accountId,
        Double amount,
        String transactionType,
        LocalDateTime timestamp)
{
}
