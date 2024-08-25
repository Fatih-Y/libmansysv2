package com.lib.libmansys.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.lib.libmansys.entity.Enum.LoanStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "loans")
@Data
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id", nullable = false)
    @JsonBackReference
    private Book book;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @Column(nullable = false)
    private LocalDate loanDate;

    @Column
    private LocalDate expectedReturnDate;

    @Column
    private LocalDate actualReturnDate;

    @Enumerated(EnumType.STRING)
    private LoanStatus status;

    public Loan() {
    }

    // All-args constructor
    public Loan(User user, Book book, LocalDate loanDate, LocalDate expectedReturnDate, LocalDate actualReturnDate, LoanStatus status) {
        this.user = user;
        this.book = book;
        this.loanDate = loanDate;
        this.expectedReturnDate = expectedReturnDate;
        this.actualReturnDate = actualReturnDate;
        this.status = status;
    }

}

