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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {


    private final LoanService loanService;
    private final UserService userService;
    private final BookService bookService;
    private final EmailService emailService;
    @Autowired
    public LoanController(LoanService loanService, UserService userService, BookService bookService, EmailService emailService) {
        this.loanService = loanService;
        this.userService = userService;
        this.bookService = bookService;
        this.emailService = emailService;
    }


    @PostMapping("/borrow")
    public ResponseEntity<String> borrowBook(@RequestParam Long userId, @RequestParam Long bookId) {
        try {
            User user = userService.getUserById((userId));
            Book book = bookService.findBooksById(bookId);
            loanService.borrowBook(user, book);
            return ResponseEntity.ok("Book borrowed successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @GetMapping("/getByUserId/{userId}")
    public ResponseEntity<List<Loan>> getLoansByUserId(@PathVariable Long userId) {
        List<Loan> loans = loanService.getLoansByUserId(userId);
        return ResponseEntity.ok(loans);
    }

    @PostMapping("/return")
    public ResponseEntity<String> returnBook(@RequestParam Long userId, @RequestParam Long bookId) {
        try {
            User user = userService.getUserById(userId);
            Book book = bookService.findBooksById(bookId);
            loanService.returnBook(user, book);
            return ResponseEntity.ok("Book returned successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to return book: " + e.getMessage());
        }
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