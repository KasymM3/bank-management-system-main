package com.bank.controller;

import com.bank.model.User;
import com.bank.model.Role;          // импортируем Role
import com.bank.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user) {
        // назначаем новую роль USER и вызываем сервис в нужной сигнатуре
        userService.registerUser(
                user.getUsername(),
                user.getPassword(),
                Role.USER
        );
        return "redirect:/login";
    }
    

    @GetMapping("/home")
    public String home() {
        return "home";
    }
}
