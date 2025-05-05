// src/main/java/com/bank/controller/AdminController.java
package com.bank.controller;

import com.bank.model.Customer;
import com.bank.service.BankService;
import com.bank.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private BankService bankService;
    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "users";
    }

//    @GetMapping("/dashboard")
//    public String dashboard(Model model) {
//        List<Customer> customers = bankService.getAllCustomers();
//        model.addAttribute("customers", customers);
//        return "admin_dashboard";
//    }

    @GetMapping("/customers/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Customer customer = bankService.getCustomerById(id);
        model.addAttribute("customer", customer);
        // передаём баланс первого счёта для заполнения поля
        model.addAttribute("balance", customer.getAccounts().get(0).getBalance());
        return "edit_customer";
    }

    @PostMapping("/customers/edit/{id}")
    public String update(
            @PathVariable Long id,
            @ModelAttribute("customer") Customer customer,
            @RequestParam("balance") Double balance
    ) {
        // сохраняем изменения имени и email
        bankService.updateCustomer(id, customer);
        // сохраняем новый баланс
        bankService.updateBalance(id, balance);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/customers/delete/{id}")
    public String delete(@PathVariable Long id) {
        bankService.deleteCustomer(id);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("dashboard")
    public String dashboard(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Double minBalance,
            @RequestParam(required = false) Double maxBalance,
            Model model) {

        List<Customer> customers = bankService.searchCustomers(name, minBalance, maxBalance);
        model.addAttribute("customers", customers);
        model.addAttribute("name", name);
        model.addAttribute("minBalance", minBalance);
        model.addAttribute("maxBalance", maxBalance);

        return "admin_dashboard";
    }
}
