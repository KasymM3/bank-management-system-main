package com.bank;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "admin123"; // Измените при необходимости
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println(encodedPassword);
    }
}
