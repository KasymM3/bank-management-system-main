package com.bank.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Schema(description = "Транзакция по счёту")
@Entity
@Table(name = "transactions")
public class Transaction {

    @Schema(description = "Уникальный идентификатор транзакции", example = "5001")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Тип транзакции", example = "Deposit")
    @Column(name = "transaction_type", nullable = false)
    private String transactionType;

    @Schema(description = "Сумма операции", example = "150.00")
    @Column(nullable = false)
    private double amount;


    @Schema(description = "Дата и время транзакции")
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime date;

    @Schema(description = "Счёт, по которому проведена транзакция")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)

    private Account account;

    // ======== Геттеры и сеттеры ========

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDateTime getDate() {
        return date;
    }

    // Сеттер для date не обязателен, Hibernate проставит его автоматически.
    // public void setDate(LocalDateTime date) { this.date = date; }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }


}
