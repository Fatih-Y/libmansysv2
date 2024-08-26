package com.lib.libmansys.dto;

import com.lib.libmansys.dto.Book.BookDTO;
import com.lib.libmansys.entity.Enum.LoanStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class LoanDTO {
    private Long id;
    private LocalDate loanDate;
    private LocalDate expectedReturnDate;
    private LoanStatus status;
    private BookDTO book;
}
