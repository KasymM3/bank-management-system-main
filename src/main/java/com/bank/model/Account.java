// src/main/java/com/bank/model/Account.java
package com.bank.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "Банковский счёт клиента")
@Entity
@Table(name = "accounts")
public class Account {

    @Schema(description = "Уникальный идентификатор счёта", example = "1001")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Тип счёта", example = "Default")
    @Column(nullable = false)
    private String accountType;

    @Schema(description = "Текущий баланс на счёте", example = "2500.75")
    @Column(nullable = false)
    private double balance;

    @OneToMany(mappedBy = "account",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Transaction> transactions = new ArrayList<>();
    @Schema(description = "Владелец счёта")
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
