// src/main/java/com/bank/controller/TransactionController.java
package com.bank.controller;

import com.bank.model.Transaction;
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
    private TransactionService transactionService;

    @Operation(
            summary = "Список транзакций",
            description = "Возвращает страницу со списком всех транзакций"
    )
    @GetMapping("/transactions")
    public String listTransactions(Model model) {
        List<Transaction> transactions = transactionService.getAllTransactions();
        model.addAttribute("transactions", transactions);
        return "transactions";
    }
}
