package com.lib.libmansys.service;

import com.lib.libmansys.entity.Book;
import com.lib.libmansys.entity.Enum.BookStatus;
import com.lib.libmansys.entity.Enum.LoanStatus;
import com.lib.libmansys.entity.Loan;
import com.lib.libmansys.entity.User;
import com.lib.libmansys.repository.BookRepository;
import com.lib.libmansys.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.lib.libmansys.entity.Enum.LoanPeriodStatus;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final UserService userService;
    private final BookService bookService;

    public LoanService(LoanRepository loanRepository, BookRepository bookRepository, UserService userService, BookService bookService) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
        this.userService = userService;
        this.bookService = bookService;
    }

    public boolean canBorrowMoreBooks(User user) {
        int activeLoanCount = loanRepository.countByUserIdAndStatus(user.getId(), LoanStatus.ACTIVE);
        return activeLoanCount < 3;
    }
// user entity yerine id al - tamamlandı
    public void borrowBook(Long userId, Long bookId) {
        User user = userService.getUserById(userId);  // Fetch user inside the method
        Book book = bookService.findBooksById(bookId);  // Fetch book inside the method

        if (!canBorrowMoreBooks(user)) {
            throw new RuntimeException("Maksimum ödünç kitap sınırına ulaşıldı.");
        }
        if (book.getStatus() != BookStatus.AVAILABLE) {
            throw new RuntimeException("Kitap ödünç alınamaz.");
        }

        LocalDate today = LocalDate.now();
        int loanDays = user.getLoanPeriodStatus() == LoanPeriodStatus.HALF ? 15 : 30;
        Loan loan = new Loan(user, book, today, today.plusDays(loanDays), null, LoanStatus.ACTIVE);
        book.setStatus(BookStatus.LOANED);
        loanRepository.save(loan);
        bookRepository.save(book);
    }

    public void returnBook(User user, Book book) {
        Loan loan = loanRepository.findByUserIdAndBookIdAndStatus(user.getId(), book.getId(), LoanStatus.ACTIVE);
        if (loan != null) {
            LocalDate now = LocalDate.now();
            loan.setActualReturnDate(now);
            if (now.isAfter(loan.getExpectedReturnDate())) {
                loan.setStatus(LoanStatus.LATE);
                // Apply penalties
                userService.applyPenalties(user);
            } else {
                loan.setStatus(LoanStatus.COMPLETED);
            }
            book.setStatus(BookStatus.AVAILABLE);
            bookRepository.save(book);
            loanRepository.save(loan);
        }
    }


    public void markOverdueLoansAsLost() {
        List<Loan> overdueLoans = loanRepository.findLoansByStatusAndExpectedReturnDateBefore(LoanStatus.ACTIVE, LocalDate.now().minusDays(30));
        for (Loan loan : overdueLoans) {
            loan.setStatus(LoanStatus.LOST);
            loanRepository.save(loan);
            userService.notifyUserOfLostBook(loan.getUser());
        }
    }
    @Transactional
    public List<Loan> findActiveLoans() {
        return loanRepository.findAllByStatus(LoanStatus.ACTIVE);
    }
    @Transactional
    public List<Loan> findPastLoans() {
        return loanRepository.findAllByStatus(LoanStatus.COMPLETED);
    }
    @Transactional
    public List<Loan> getLoansByUserId(Long userId) {
        return loanRepository.findByUserId(userId);
    }

}
