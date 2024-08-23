package com.lib.libmansys.controller;

import com.lib.libmansys.dto.LoanRequest;
import com.lib.libmansys.entity.Book;
import com.lib.libmansys.entity.Enum.LoanPeriodStatus;
import com.lib.libmansys.entity.Loan;
import com.lib.libmansys.entity.User;
import com.lib.libmansys.service.BookService;
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
    @Autowired
    public LoanController(LoanService loanService, UserService userService, BookService bookService) {
        this.loanService = loanService;
        this.userService = userService;
        this.bookService = bookService;
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
    @GetMapping
    public ResponseEntity<List<Loan>> getAllLoans() {
        List<Loan> loans = loanService.findAllActiveLoans();
        return ResponseEntity.ok(loans);
    }
}