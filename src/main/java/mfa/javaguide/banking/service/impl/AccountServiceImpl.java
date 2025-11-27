package mfa.javaguide.banking.service.impl;

import mfa.javaguide.banking.dto.AccountDto;
import mfa.javaguide.banking.dto.TransactionDto;
import mfa.javaguide.banking.dto.TransferFundDto;
import mfa.javaguide.banking.entity.Account;
import mfa.javaguide.banking.entity.Transaction;
import mfa.javaguide.banking.exception.AccountException;
import mfa.javaguide.banking.mapper.AccountMapper;
import mfa.javaguide.banking.repository.AccountRepository;
import mfa.javaguide.banking.repository.TransactionRepository;
import mfa.javaguide.banking.service.AccountService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {
    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;
    private static final String TRANSACTION_TYPE_DEPOSIT = "DEPOSIT";
    private static final String TRANSACTION_TYPE_WITHDRAWAL = "WITHDRAWAL";
    private static final String TRANSACTION_TYPE_TRANSFER = "TRANSFER";


    public AccountServiceImpl(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }


    @Override
    public AccountDto createAccount(AccountDto accountDto) {
        Account account = AccountMapper.mapToAccount(accountDto);
        Account savedAccount = accountRepository.save(account);
        return AccountMapper.mapToAccountDto(savedAccount);
    }

    public AccountDto getAccountById(Long id){
        Account savedAccount = accountRepository.findById(id).orElseThrow(()-> new AccountException("Account not found"));
        return AccountMapper.mapToAccountDto(savedAccount);
    }

    @Override
    public AccountDto deposit(Long id, double amount) {
        Account account = accountRepository.findById(id).orElseThrow(()-> new AccountException("Account not found"));
        double total = account.getBalance() + amount;
        account.setBalance(total);
       Account savedAccount= accountRepository.save(account);

       // Here you can also create and save a Transaction record for the deposit
        Transaction transaction = new Transaction();
        transaction.setAccountId(id);
        transaction.setAmount(amount);
        transaction.setTransactionType(TRANSACTION_TYPE_DEPOSIT);
        transaction.setTimestamp(java.time.LocalDateTime.now());
        // You would typically save this transaction using a TransactionRepository
        transactionRepository.save(transaction);
        return AccountMapper.mapToAccountDto(savedAccount);
    }

    @Override
    public AccountDto withdraw(Long id, double amount) {
        Account account = accountRepository.findById(id).orElseThrow(()-> new AccountException("Account not found"));
        if (account.getBalance() < amount){
            throw new RuntimeException("Insufficient balance");
        }
        double total = account.getBalance() - amount;
        account.setBalance(total);
        Account savedAccount= accountRepository.save(account);
        // Here you can also create and save a Transaction record for the deposit
        Transaction transaction = new Transaction();
        transaction.setAccountId(id);
        transaction.setAmount(amount);
        transaction.setTransactionType(TRANSACTION_TYPE_WITHDRAWAL);
        transaction.setTimestamp(java.time.LocalDateTime.now());
        // You would typically save this transaction using a TransactionRepository
        transactionRepository.save(transaction);


        return AccountMapper.mapToAccountDto(savedAccount);

    }

    @Override
    public List<AccountDto> getAllAccounts() {
       List<Account> accounts= accountRepository.findAll();
       return accounts.stream().map((account) -> AccountMapper.mapToAccountDto(account)).toList();
    }

    @Override
    public void deleteAccount(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(()-> new AccountException("Account not found"));
        accountRepository.delete(account);
    }

    @Override
    public void transferFunds(TransferFundDto transferFundDto) {
        //check if fromAccount exists
        Account fromAccount = accountRepository.findById(transferFundDto.fromAccountId())
                .orElseThrow(() -> new AccountException("From Account not found"));


        //check if toAccount exists
        Account toAccount = accountRepository.findById(transferFundDto.toAccountId())
                .orElseThrow(() -> new AccountException("ToAccount not found"));


        // check if fromAccount has insufficient balance
        if (fromAccount.getBalance() < transferFundDto.amount()) {
            throw new RuntimeException("Insufficient balance in sender's account");
        }


        // Debit amount From's account
        fromAccount.setBalance(fromAccount.getBalance() - transferFundDto.amount());

        // Add amount  To's ToAccount
        toAccount.setBalance(toAccount.getBalance() + transferFundDto.amount());

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        // Here you can also create and save a Transaction record for the deposit
        Transaction transaction = new Transaction();
        transaction.setAccountId(transferFundDto.fromAccountId());
        transaction.setAmount(transferFundDto.amount());
        transaction.setTransactionType(TRANSACTION_TYPE_TRANSFER);
        transaction.setTimestamp(java.time.LocalDateTime.now());
        // You would typically save this transaction using a TransactionRepository
        transactionRepository.save(transaction);
    }

    @Override
    public List<TransactionDto> getAccountTransactions(Long accountId) {
        List<Transaction> transactions = transactionRepository.findByAccountIdOrderByTimestampDesc(accountId);
     return transactions.stream().map((transaction) -> convertEntityToDto(transaction)).toList();

    }

    private TransactionDto convertEntityToDto(Transaction transaction) {
        TransactionDto transactionDto = new TransactionDto(
        transaction.getId(),
        transaction.getAccountId(),
        transaction.getAmount(),
        transaction.getTransactionType(),
        transaction.getTimestamp());
        return transactionDto;
    }
}
