package mfa.javaguide.banking.dto;

public record TransferFundDto(Long fromAccountId, Long toAccountId, double amount) {
}
