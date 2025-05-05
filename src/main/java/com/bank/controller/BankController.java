// src/main/java/com/bank/controller/BankController.java
package com.bank.controller;

import com.bank.model.Customer;
import com.bank.model.Transaction;
import com.bank.repository.TransactionRepository;
import com.bank.service.BankService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Customers", description = "Управление клиентами банка")
@Controller
public class BankController {

    @Autowired
    private BankService bankService;

    @Operation(
            summary = "Добавить клиента",
            description = "Создаёт нового клиента и автоматически создаёт для него стандартный счёт"
    )
    @PostMapping("/create-customer")
    public String createCustomer(
            @RequestParam("name") String name,
            @RequestParam("email") String email
    ) {
        Customer customer = new Customer();
        customer.setName(name);
        customer.setEmail(email);
        bankService.createCustomer(customer);
        return "redirect:/customers";
    }

    @Operation(
            summary = "Список клиентов",
            description = "Возвращает страницу со списком всех клиентов"
    )
    @GetMapping("/customers")
    public String listCustomers(Model model) {
        List<Customer> customers = bankService.getAllCustomers();
        model.addAttribute("customers", customers);
        return "customers";
    }

    @Tag(name = "Transactions", description = "Операции по счетам клиентов")
    @Operation(
            summary = "Создать транзакцию",
            description = "Пополнение или снятие средств со счёта клиента"
    )
    @PostMapping("/create-transaction")
    public String createTransaction(
            @RequestParam("customerId") Long customerId,
            @RequestParam("transactionType") String transactionType,
            @RequestParam("amount") double amount
    ) {
        if ("Deposit".equalsIgnoreCase(transactionType)) {
            bankService.depositForCustomer(customerId, amount);
        } else {
            bankService.withdrawForCustomer(customerId, amount);
        }
        return "redirect:/transactions";
    }

    @Operation(
            summary = "Форма создания транзакции",
            description = "Отображает список клиентов для выбора при создании транзакции"
    )
    @GetMapping("/create-transaction")
    public String showCreateTransactionForm(Model model) {
        List<Customer> customers = bankService.getAllCustomers();
        model.addAttribute("customers", customers);
        return "createTransaction";
    }

}
