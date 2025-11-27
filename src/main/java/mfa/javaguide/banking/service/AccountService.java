package mfa.javaguide.banking.service;

import mfa.javaguide.banking.dto.AccountDto;
import mfa.javaguide.banking.dto.TransactionDto;
import mfa.javaguide.banking.dto.TransferFundDto;

import java.util.List;

public interface AccountService {
    AccountDto createAccount(AccountDto accountDto);
    public AccountDto getAccountById(Long id);
    public AccountDto deposit(Long id, double amount);
    public AccountDto withdraw(Long id, double amount);
    public List<AccountDto>getAllAccounts();
    public void deleteAccount(Long id);
    public void transferFunds(TransferFundDto transferFundDto);
    public List<TransactionDto> getAccountTransactions(Long accountId);

}
