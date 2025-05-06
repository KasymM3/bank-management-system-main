// src/main/java/com/bank/controller/BankController.java
package com.bank.controller;

import com.bank.model.Customer;
import com.bank.model.Transaction;
import com.bank.repository.TransactionRepository;
import com.bank.service.BankService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Sort;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Tag(name = "Customers", description = "Управление клиентами банка")
@Controller
public class BankController {

    @Autowired
    private BankService bankService;

    @Operation(
            summary = "Добавить клиента",
            description = "Создаёт нового клиента и автоматически создаёт для него стандартный счёт"
    )
    @PostMapping("/create-customer")
    public String createCustomer(
            @RequestParam("name") String name,
            @RequestParam("email") String email
    ) {
        Customer customer = new Customer();
        customer.setName(name);
        customer.setEmail(email);
        bankService.createCustomer(customer);
        return "redirect:/customers";
    }

    @Operation(
            summary = "Список клиентов",
            description = "Возвращает страницу со списком всех клиентов"
    )
    @GetMapping("/customers")
    public String listCustomers(Model model) {
        List<Customer> customers = bankService.getAllCustomers();
        model.addAttribute("customers", customers);
        return "customers";
    }

    @Tag(name = "Transactions", description = "Операции по счетам клиентов")
    @Operation(
            summary = "Создать транзакцию",
            description = "Пополнение или снятие средств со счёта клиента"
    )
    @PostMapping("/create-transaction")
    public String createTransaction(
            @RequestParam("customerId") Long customerId,
            @RequestParam("transactionType") String transactionType,
            @RequestParam("amount") double amount
    ) {
        if ("Deposit".equalsIgnoreCase(transactionType)) {
            bankService.depositForCustomer(customerId, amount);
        } else {
            bankService.withdrawForCustomer(customerId, amount);
        }
        return "redirect:/transactions";
    }

    @Operation(
            summary = "Форма создания транзакции",
            description = "Отображает список клиентов для выбора при создании транзакции"
    )
    @GetMapping("/create-transaction")
    public String showCreateTransactionForm(Model model) {
        List<Customer> customers = bankService.getAllCustomers();
        model.addAttribute("customers", customers);
        return "createTransaction";
    }

    @Operation(
            summary = "Экспорт клиентов в CSV",
            description = "Генерирует CSV-файл со списком всех клиентов"
    )
    @GetMapping("/customers/export")
    public void exportCustomersToCSV(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"customers.csv\"");
        List<Customer> customers = bankService.getAllCustomers();
        PrintWriter writer = response.getWriter();
        // Заголовок CSV
        writer.println("ID,Name,Email");
        for (Customer c : customers) {
            writer.println(c.getId() + "," + c.getName() + "," + c.getEmail());
        }
        writer.flush();
    }

    @Autowired
    private JavaMailSender mailSender;
    @Value("${app.mail.recipient}")
    private String recipient;

    // уже был метод exportCustomersToCSV(...)
    @GetMapping("/customers/email")
    public String sendCustomersExcelByEmail() throws IOException, MessagingException {
        List<Customer> list = bankService.getAllCustomers();

        // 1. Формируем Excel
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("Customers");
        var header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Name");
        header.createCell(2).setCellValue("Email");

        int rowIdx = 1;
        for (Customer c : list) {
            var row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(c.getId());
            row.createCell(1).setCellValue(c.getName());
            row.createCell(2).setCellValue(c.getEmail());
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        wb.write(baos);
        wb.close();

        // 2. Готовим письмо
        MimeMessage msg = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
        helper.setTo(recipient);
        helper.setSubject("Отчет по клиентам — Excel");
        helper.setText("Во вложении файл customers.xlsx с данными всех клиентов.");
        helper.addAttachment("customers.xlsx", new ByteArrayResource(baos.toByteArray()));

        mailSender.send(msg);

        // перенаправляем обратно на страницу клиентов
        return "redirect:/customers";
    }
}
