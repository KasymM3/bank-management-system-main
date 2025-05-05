package com.bank.repository;

import com.bank.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("""
        SELECT t
        FROM Transaction t
        JOIN FETCH t.account a
        JOIN FETCH a.customer c
        ORDER BY t.date DESC
    """)
    List<Transaction> findAllWithAccountAndCustomer();
}