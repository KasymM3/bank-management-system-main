// src/main/java/com/bank/controller/TransactionController.java
package com.bank.controller;

import com.bank.model.Transaction;
import com.bank.service.BankService;
import com.bank.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Tag(name = "Transactions", description = "Просмотр транзакций")
@Controller
public class TransactionController {

    @Autowired
    private BankService bankService;

    @Tag(name = "Transactions", description = "Отображение истории транзакций")
    @Operation(summary = "Список транзакций", description = "Возвращает страницу со всеми транзакциями")
    @GetMapping("/transactions")
    public String listTransactions(Model model) {
        List<Transaction> transactions = bankService.getAllTransactions();
        model.addAttribute("transactions", transactions);
        return "transactions";
    }
}
