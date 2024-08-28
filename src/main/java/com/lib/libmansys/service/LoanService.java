package com.lib.libmansys.service;

import com.lib.libmansys.entity.Book;
import com.lib.libmansys.entity.Enum.BookStatus;
import com.lib.libmansys.entity.Enum.LoanStatus;
import com.lib.libmansys.entity.Loan;
import com.lib.libmansys.entity.User;
import com.lib.libmansys.repository.BookRepository;
import com.lib.libmansys.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.lib.libmansys.entity.Enum.LoanPeriodStatus;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository; //todo: too many dependencies
    private final BookRepository bookRepository;
    private final UserService userService;
    private final BookService bookService;

    private static final int MAX_ACTIVE_LOANS = 3;

    public boolean canBorrowMoreBooks(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("Kullanıcı id boş bırakılamaz");
        }
        int activeLoanCount = loanRepository.countByUserIdAndStatus(userId, LoanStatus.ACTIVE);
        return activeLoanCount < MAX_ACTIVE_LOANS;
    }
// user entity yerine id al - tamamlandı
    public void borrowBook(Long userId, Long bookId) {
        User user = userService.getUserById(userId);
        Book book = bookService.findBooksById(bookId);

        if (!canBorrowMoreBooks(userId)) {
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

    public String returnBook(Long userId, Long bookId) {
        User user = userService.getUserById(userId);
        Book book = bookService.findBooksById(bookId);

        List<LoanStatus> statuses = Arrays.asList(LoanStatus.ACTIVE, LoanStatus.LATE);
        List<Loan> loans = loanRepository.findByUserIdAndBookIdAndStatusIn(user.getId(), book.getId(), statuses);

        if (loans.isEmpty()) {
            throw new RuntimeException("Kullanıcının bu kitap için aktif veya geç ödünç işlemi yok.");
        }

        Loan loan = loans.stream()
                .filter(l -> l.getBook().getId().equals(bookId))
                .findFirst()  // optional loan
                .orElseThrow(() -> new RuntimeException("No active or late loan found for this book."));

        if (book.getStatus() != BookStatus.LOANED) {
            throw new RuntimeException("Bu kitap ödünç verilmemiş.");
        }

        LocalDate now = LocalDate.now();
        loan.setActualReturnDate(now);
        if (now.isAfter(loan.getExpectedReturnDate())) {
            loan.setStatus(LoanStatus.LATE);
            userService.applyPenalties(user);
        } else {
            loan.setStatus(LoanStatus.COMPLETED);
        }
        book.setStatus(BookStatus.AVAILABLE);
        bookRepository.save(book);
        loanRepository.save(loan);

        return "Kitap başarıyla iade edildi.";
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

