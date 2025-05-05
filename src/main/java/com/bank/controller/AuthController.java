// src/main/java/com/bank/controller/AuthController.java
package com.bank.controller;

import com.bank.model.ActivityLog;
import com.bank.model.User;
import com.bank.model.Role;
import com.bank.repository.ActivityLogRepository;
import com.bank.repository.UserRepository;
import com.bank.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired private UserRepository userRepository;
    @Autowired private ActivityLogRepository activityLogRepository;

    // Показываем форму регистрации
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", Role.values());
        return "register";
    }

    // Обрабатываем отправку формы
    @PostMapping("/register")
    public String register(@ModelAttribute("user") User userForm) {
        User saved = userRepository.save(userForm);

        // Логируем регистрацию
        ActivityLog log = new ActivityLog();
        log.setUser(saved);
        log.setAction("Создал аккаунт");
        log.setDate(LocalDateTime.now());
        activityLogRepository.save(log);

        return "redirect:/login";
    }

    @GetMapping("/home")
    public String home(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object denied = session.getAttribute("accessDenied");
            if (denied != null) {
                model.addAttribute("accessDenied", denied);
                session.removeAttribute("accessDenied");
            }
        }
        return "home";
    }

    @GetMapping
    public String showLoginPage() {
        return "login";
    }
}
