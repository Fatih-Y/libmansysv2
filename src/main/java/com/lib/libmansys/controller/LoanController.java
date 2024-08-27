package com.lib.libmansys.controller;

import com.lib.libmansys.dto.EmailRequest;
import com.lib.libmansys.dto.LoanRequest;
import com.lib.libmansys.entity.Book;
import com.lib.libmansys.entity.Enum.LoanPeriodStatus;
import com.lib.libmansys.entity.Loan;
import com.lib.libmansys.entity.User;
import com.lib.libmansys.service.BookService;
import com.lib.libmansys.service.EmailService;
import com.lib.libmansys.service.LoanService;
import com.lib.libmansys.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService; //todo: too many dependencies
    private final UserService userService;
    private final BookService bookService;
    private final EmailService emailService;


    @PostMapping("/borrow")
    public ResponseEntity<String> borrowBook(@RequestParam Long userId, @RequestParam Long bookId) {
        try {
            loanService.borrowBook(userId, bookId);
            return ResponseEntity.ok("Kitap başarıyla ödünç alındı.");
        } catch (RuntimeException e) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            if (e.getMessage().contains("Kitap ödünç alınamaz.")) {
                status = HttpStatus.CONFLICT;
            } else if (e.getMessage().contains("Maksimum ödünç kitap sınırına ulaşıldı.")) {
                status = HttpStatus.FORBIDDEN;
            }
            return ResponseEntity.status(status).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Beklenmeyen bir hata oluştu.");
        }
    }

    @PostMapping("/return")
    public ResponseEntity<String> returnBook(@RequestParam Long userId, @RequestParam Long bookId) {
        try {
            String result = loanService.returnBook(userId, bookId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            if (e.getMessage().contains("Kullanıcının böyle bir ödünç işlemi yok.")) {
                status = HttpStatus.NOT_FOUND;
            } else if (e.getMessage().contains("kitap ödünç verilmemiş.")) {
                status = HttpStatus.CONFLICT;
            }
            return ResponseEntity.status(status).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Beklenmeyen bir hata oluştu: " + e.getMessage());
        }
    }

    @GetMapping("/getByUserId/{userId}")
    public ResponseEntity<List<Loan>> getLoansByUserId(@PathVariable Long userId) {
        List<Loan> loans = loanService.getLoansByUserId(userId);
        return ResponseEntity.ok(loans);
    }


    @GetMapping("/check-overdue")
    public ResponseEntity<String> checkAndMarkOverdueLoans() {
        loanService.markOverdueLoansAsLost();
        return ResponseEntity.ok("Overdue loans checked and updated.");
    }
    @GetMapping("/activeLoans")
    public ResponseEntity<List<Loan>> getActiveLoans() {
        List<Loan> loans = loanService.findActiveLoans();
        return ResponseEntity.ok(loans);
    }
    @GetMapping("/pastLoans")
    public ResponseEntity<List<Loan>> getPastLoans() {
        List<Loan> loans = loanService.findPastLoans();
        return ResponseEntity.ok(loans);
    }
    @PostMapping("/sendEmail")
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequest emailRequest) {
        try {
            emailService.sendEmail(emailRequest.getTo(), emailRequest.getSubject(), emailRequest.getContent());
            return ResponseEntity.ok("Email sent successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send email: " + e.getMessage());
        }
    }
}