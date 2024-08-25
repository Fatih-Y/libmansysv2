package com.lib.libmansys.repository;
import com.lib.libmansys.entity.Enum.LoanStatus;
import com.lib.libmansys.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {


    int countActiveLoansByUserId(Long userId);

    List<Loan> findLoansByUserIdAndStatus(Long userId, LoanStatus status);
    List<Loan> findByUserId(Long userId);

    Loan findByUserIdAndBookIdAndStatus(Long userId, Long bookId, LoanStatus status);


    List<Loan> findAllByStatus(LoanStatus status);
    List<Loan> findLoansByStatusAndExpectedReturnDateBefore(LoanStatus status, LocalDate date);
}
