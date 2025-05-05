package com.bank.service;

import com.bank.model.*;
import com.bank.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BankService {

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @Autowired
    private UserRepository userRepository;


    public List<Customer> getAllCustomers() {
        return customerRepo.findAll();
    }

    private void logActivity(String action) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        ActivityLog log = new ActivityLog();
        log.setUser(user);
        log.setAction(action);
        log.setDate(LocalDateTime.now());

        activityLogRepository.save(log);
    }

    public void createCustomer(Customer customer) {
        Customer savedCustomer = customerRepo.save(customer);

        Account account = new Account();
        account.setAccountType("Default");
        account.setBalance(0.0);
        account.setCustomer(savedCustomer);
        accountRepo.save(account);

        logActivity("Создал клиента");
    }

    public void depositForCustomer(Long customerId, double amount) {
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerId));
        Account account = findOrCreateAccount(customer);
        account.setBalance(account.getBalance() + amount);
        accountRepo.save(account);

        Transaction transaction = new Transaction();
        transaction.setTransactionType("Deposit");
        transaction.setAmount(amount);
        transaction.setAccount(account);
        transactionRepository.save(transaction);

        logActivity("Сделал транзакцию");
    }

    public void withdrawForCustomer(Long customerId, double amount) {
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerId));
        Account account = findOrCreateAccount(customer);
        if (account.getBalance() < amount) {
            throw new RuntimeException("Insufficient funds!");
        }
        account.setBalance(account.getBalance() - amount);
        accountRepo.save(account);

        Transaction transaction = new Transaction();
        transaction.setTransactionType("Withdrawal");
        transaction.setAmount(amount);
        transaction.setAccount(account);
        transactionRepository.save(transaction);

        logActivity("Сделал транзакцию");
    }
    private Account findOrCreateAccount(Customer customer) {
        Account account;
        if (customer.getAccounts() == null || customer.getAccounts().isEmpty()) {
            account = new Account();
            account.setCustomer(customer);
            // присваиваем строковый тип счёта
            account.setAccountType("checking");
            account.setBalance(0.0);
            account = accountRepo.save(account);
        } else {
            account = customer.getAccounts().get(0);
        }
        return account;
    }



    public Customer getCustomerById(Long id) {
        return customerRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Клиент не найден: " + id));
    }

    public void updateCustomer(Long id, Customer customer) {

        Customer existing = getCustomerById(id);
        existing.setName(customer.getName());
        existing.setEmail(customer.getEmail());
        // при необходимости — другие поля
        customerRepo.save(existing);
        logActivity("Обновил клиента");
    }

    public void deleteCustomer(Long id) {
        customerRepo.deleteById(id);
        logActivity("Удалил клиента");
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAllWithAccountAndCustomer();
    }

    public List<Customer> searchCustomers(String name, Double minBalance, Double maxBalance) {
        // Сначала получаем всех или по имени
        List<Customer> list = (name == null || name.isBlank())
                ? customerRepo.findAll()
                : customerRepo.findByNameContainingIgnoreCase(name);

        // Если нет фильтра по балансу — возвращаем результат
        if (minBalance == null && maxBalance == null) {
            return list;
        }

        // Иначе отбираем тех, у кого есть хотя бы один счёт в диапазоне
        return list.stream()
                .filter(c -> {
                    // получаем все аккаунты клиента
                    List<Account> accounts = accountRepo.findByCustomerId(c.getId());
                    return accounts.stream().anyMatch(a -> {
                        boolean ge = (minBalance == null) || a.getBalance() >= minBalance;
                        boolean le = (maxBalance == null) || a.getBalance() <= maxBalance;
                        return ge && le;
                    });
                })
                .toList();
    }

    public void updateBalance(Long customerId, Double newBalance) {
        Customer customer = getCustomerById(customerId);
        Account account = customer.getAccounts().stream().findFirst()
                .orElseGet(() -> {
                    Account acc = new Account();
                    acc.setCustomer(customer);
                    return acc;
                });
        account.setBalance(newBalance);
        accountRepo.save(account);
    }

}
