// src/main/java/com/bank/controller/AccountController.java
package com.bank.controller;

import com.bank.model.Account;
import com.bank.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Tag(name = "Accounts", description = "Управление банковскими счетами")
@Controller
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Operation(
            summary = "Список счетов",
            description = "Возвращает страницу со списком всех банковских счетов"
    )
    @GetMapping("/accounts")
    public String listAccounts(Model model) {
        List<Account> accounts = accountService.getAllAccounts();
        model.addAttribute("accounts", accounts);
        return "accounts";
    }
}
