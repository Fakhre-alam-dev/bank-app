package mfa.javaguide.banking.exception;

public class AccountException extends RuntimeException{
    public AccountException(String message) {
        super(message);
    }
}
