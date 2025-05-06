// src/main/java/com/bank/controller/TransactionController.java
package com.bank.controller;

import com.bank.model.Transaction;
import com.bank.service.BankService;
import com.bank.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Tag(name = "Transactions", description = "Просмотр транзакций")
@Controller
public class TransactionController {

    @Autowired
    private BankService bankService;
    @Autowired
    private TransactionService transactionService;

    @Tag(name = "Transactions", description = "Отображение истории транзакций")
    @Operation(summary = "Список транзакций", description = "Возвращает страницу со всеми транзакциями")
    @GetMapping("/transactions")
    public String listTransactions(Model model) {
        List<Transaction> transactions = bankService.getAllTransactions();
        model.addAttribute("transactions", transactions);
        return "transactions";
    }

    @Operation(
            summary = "Экспорт транзакций в CSV",
            description = "Генерирует CSV-файл со списком всех транзакций"
    )
    @GetMapping("/transactions/export")
    public void exportTransactionsToCSV(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"transactions.csv\"");

        List<Transaction> transactions = transactionService.getAllTransactions();
        try (PrintWriter writer = response.getWriter()) {
            // Заголовок CSV
            writer.println("ID,AccountID,Amount,Date,Type");
            for (Transaction t : transactions) {
                writer.println(
                        t.getId() + "," +
                                t.getAccount().getId() + "," +
                                t.getAmount() + "," +
                                t.getDate() + "," +
                                t.getTransactionType()
                );
            }
        }
    }

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.mail.recipient}")
    private String recipient;

    // Уже есть экспорт CSV...
    // Теперь — экспорт Excel и отправка по почте:
    @GetMapping("/transactions/email")
    public String sendTransactionsExcelByEmail() throws IOException, MessagingException {
        List<Transaction> txs = transactionService.getAllTransactions();

        // 1. Собираем .xlsx
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("Transactions");
        var header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Клиент");
        header.createCell(2).setCellValue("Тип");
        header.createCell(3).setCellValue("Сумма");
        header.createCell(4).setCellValue("Дата");

        int rowIdx = 1;
        for (Transaction t : txs) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(t.getId());
            row.createCell(1).setCellValue(t.getAccount().getCustomer().getName());
            row.createCell(2).setCellValue(t.getTransactionType().toString());
            // <-- здесь важно именно так:
            row.createCell(3).setCellValue(t.getAmount());
            row.createCell(4).setCellValue(t.getDate().toString());
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        wb.write(baos);
        wb.close();

        // 2. Формируем письмо с вложением
        MimeMessage msg = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
        helper.setTo(recipient);
        helper.setSubject("Отчет по транзакциям — Excel");
        helper.setText("Во вложении файл transactions.xlsx с данными всех транзакций.");
        helper.addAttachment("transactions.xlsx", new ByteArrayResource(baos.toByteArray()));

        mailSender.send(msg);

        // возвращаемся на страницу транзакций
        return "redirect:/transactions";
    }
}
