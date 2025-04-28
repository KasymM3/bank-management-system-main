package com.bank.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "Клиент банка")
@Entity
@Table(name = "customers")
public class Customer {

    @Schema(description = "Уникальный идентификатор клиента", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Имя клиента", example = "Иван Иванов")
    @Column(nullable = false)
    private String name;

    @Schema(description = "Email клиента", example = "ivan@example.com")
    @Column(nullable = false, unique = true)
    private String email;

    @Schema(description = "Список счетов клиента")
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Account> accounts = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }
}
