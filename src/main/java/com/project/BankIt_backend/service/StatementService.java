package com.project.BankIt_backend.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.project.BankIt_backend.entity.Account;
import com.project.BankIt_backend.entity.Transaction;
import com.project.BankIt_backend.entity.User;
import com.project.BankIt_backend.repository.AccountRepository;
import com.project.BankIt_backend.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatementService {

    private final UserService userService;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public byte[] generateStatementPdf(
            LocalDate startDate,
            LocalDate endDate) throws Exception {

        String html =
                generateStatementHtml(
                        startDate,
                        endDate
                );

        ByteArrayOutputStream outputStream =
                new ByteArrayOutputStream();

        PdfRendererBuilder builder =
                new PdfRendererBuilder();

        builder.withHtmlContent(
                html,
                null
        );

        builder.toStream(outputStream);

        builder.run();

        return outputStream.toByteArray();
    }

    public String generateStatementHtml(LocalDate startDate, LocalDate endDate) throws IOException {

        InputStream inputStream = getClass()
                .getClassLoader()
                .getResourceAsStream("templates/statement.html");

        if(inputStream == null){
            throw new RuntimeException("statement.html not found");
        }

        String html = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        User currentUser = userService.getCurrentUser();

        List<Transaction> transactions = transactionRepository.getStatementTransactions(
                currentUser.getUserId(),
                startDate.atStartOfDay(),
                endDate.atTime(23,59,59)
        );

        List<Account> accounts = accountRepository.findByUser_UserId(currentUser.getUserId());
        Account primaryAccount = accounts.get(0);

        BigDecimal totalBalance = accounts.stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSent = transactionRepository.getTotalSent(accounts);
        BigDecimal totalReceived = transactionRepository.getTotalReceived(accounts);

        long totalTransactions = transactionRepository
                .countBySenderAccountInOrReceiverAccountIn(accounts, accounts);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

        html = html.replace("{{GENERATED_DATE}}", LocalDate.now().format(dateFormatter));
        html = html.replace("{{START_DATE}}", startDate.format(dateFormatter));
        html = html.replace("{{END_DATE}}", endDate.format(dateFormatter));

        // Assuming your User entity has these getters
        html = html.replace("{{FULL_NAME}}", currentUser.getFullName());
        html = html.replace("{{EMAIL}}", currentUser.getEmail());
        html = html.replace("{{PHONE_NUMBER}}", currentUser.getPhoneNumber());
        html = html.replace("{{ACCOUNT_NUMBER}}", primaryAccount.getAccountNo());

        // 2. REPLACING TOTALS
        html = html.replace("{{CURRENT_BALANCE}}", totalBalance.toString());
        html = html.replace("{{TOTAL_SENT}}", totalSent.toString());
        html = html.replace("{{TOTAL_RECEIVED}}", totalReceived.toString());
        html = html.replace("{{TOTAL_TRANSACTIONS}}", String.valueOf(totalTransactions));
        html = html.replace("{{CLOSING_BALANCE}}", totalBalance.toString());

        StringBuilder transactionRows = new StringBuilder();

        for(Transaction transaction : transactions) {

            // Determine amount color (Green for positive/received, Dark for negative/sent)
            boolean isPositive =
                    transaction.getReceiverAccount()
                            .getUser()
                            .getUserId()
                            .equals(currentUser.getUserId());
            String amountClass = isPositive ? "amount-positive" : "amount-negative";
            String amountPrefix = isPositive ? "+" : ""; // Negative numbers usually have their own minus sign

            // Determine badge color based on status
            String statusLower = transaction.getStatus().toLowerCase();
            String badgeClass =
                    statusLower.equals("success")
                            ? "badge-completed"
                            : "badge-pending";

            transactionRows.append("""
            <tr>
                <td class="col-date">%s</td>
                <td class="col-type">%s</td>
                <td class="col-ref">%s</td>
                <td>%s</td>
                <td class="col-amount %s">%s%s</td>
                <td class="col-status"><span class="badge %s">%s</span></td>
            </tr>
            """.formatted(
                    transaction.getTransactionDate().format(dateFormatter), // Format date nicely
                    transaction.getTransactionType(),
                    transaction.getReferenceNumber(),
                    transaction.getDescription(),
                    amountClass,
                    amountPrefix,
                    transaction.getAmount(),
                    badgeClass,
                    transaction.getStatus()
            ));
        }

        html = html.replace("{{TRANSACTION_ROWS}}", transactionRows.toString());

        return html;
    }
}
