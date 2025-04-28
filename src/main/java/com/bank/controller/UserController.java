// src/main/java/com/bank/controller/UserController.java
package com.bank.controller;

import com.bank.service.AccountService;
import com.bank.service.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private BankService bankService;

    @GetMapping("/accounts")
    public String listAccounts(Model model) {
        model.addAttribute("accounts", accountService.getAllAccounts());
        return "accounts-user";
    }

    // Заменили withdraw → withdrawForCustomer
    @PostMapping("/withdraw")
    public String withdraw(
            @RequestParam("customerId") Long customerId,
            @RequestParam("amount") double amount) {
        bankService.withdrawForCustomer(customerId, amount);
        return "redirect:/user/accounts";
    }

    // Заменили deposit → depositForCustomer
    @PostMapping("/deposit")
    public String deposit(
            @RequestParam("customerId") Long customerId,
            @RequestParam("amount") double amount) {
        bankService.depositForCustomer(customerId, amount);
        return "redirect:/user/accounts";
    }
}
