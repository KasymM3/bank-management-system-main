package com.bank.service;

import com.bank.model.Account;
import com.bank.model.Customer;
import com.bank.model.Transaction;
import com.bank.repository.AccountRepository;
import com.bank.repository.CustomerRepository;
import com.bank.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BankService {

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private TransactionRepository transactionRepository;

    public List<Customer> getAllCustomers() {
        return customerRepo.findAll();
    }

    public void createCustomer(Customer customer) {
        // Сохраняем клиента
        Customer savedCustomer = customerRepo.save(customer);
        // Автоматически создаём стандартный счёт для клиента
        Account account = new Account();
        account.setAccountType("Default");
        account.setBalance(0.0);
        account.setCustomer(savedCustomer);
        accountRepo.save(account);
    }

    public void depositForCustomer(Long customerId, double amount) {
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found!"));
        if (customer.getAccounts() == null || customer.getAccounts().isEmpty()) {
            throw new RuntimeException("Customer has no account!");
        }
        Account account = customer.getAccounts().get(0); // Используем первый счёт
        account.setBalance(account.getBalance() + amount);
        accountRepo.save(account);

        Transaction transaction = new Transaction();
        transaction.setTransactionType("Deposit");
        transaction.setAmount(amount);
        transaction.setAccount(account);
        transactionRepository.save(transaction);
    }

    public void withdrawForCustomer(Long customerId, double amount) {
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found!"));
        if (customer.getAccounts() == null || customer.getAccounts().isEmpty()) {
            throw new RuntimeException("Customer has no account!");
        }
        Account account = customer.getAccounts().get(0);
        if (account.getBalance() >= amount) {
            account.setBalance(account.getBalance() - amount);
            accountRepo.save(account);

            Transaction transaction = new Transaction();
            transaction.setTransactionType("Withdrawal");
            transaction.setAmount(amount);
            transaction.setAccount(account);
            transactionRepository.save(transaction);
        } else {
            throw new RuntimeException("Insufficient funds!");
        }
    }
}
