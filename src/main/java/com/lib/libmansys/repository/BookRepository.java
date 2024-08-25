package com.lib.libmansys.repository;

import com.lib.libmansys.entity.Book;
import com.lib.libmansys.entity.Enum.BookStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    Page<Book> findAll(Pageable pageable);
    Page<Book> findByStatus(BookStatus status, Pageable pageable);
    List<Book> findByTitleContainingIgnoreCase(String title);
    Page<Book> findByGenresName(String name, Pageable pageable);
    Page<Book> findByPublishersName(String name, Pageable pageable);
    List<Book> findByStatus(BookStatus status);
    Page<Book> findByAuthorsFirstName(String firstName, Pageable pageable);
    Page<Book> findByTitle(String title, Pageable pageable);

    Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<Book> findByAuthorsFirstNameContainingIgnoreCase(String author, Pageable pageable);

    Page<Book> findByGenresNameContainingIgnoreCase(String genre, Pageable pageable);

    Page<Book> findByPublishersNameContainingIgnoreCase(String publisher, Pageable pageable);

//    List<Book> findByAuthors_LastName(String lastName);
}
