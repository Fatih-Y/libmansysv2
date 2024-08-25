package com.lib.libmansys.service;

import com.lib.libmansys.dto.CreateBookRequest;
import com.lib.libmansys.entity.Author;
import com.lib.libmansys.entity.Book;
import com.lib.libmansys.entity.Enum.BookStatus;
import com.lib.libmansys.entity.Genre;
import com.lib.libmansys.entity.Publisher;
import com.lib.libmansys.repository.AuthorRepository;
import com.lib.libmansys.repository.BookRepository;
import com.lib.libmansys.repository.GenreRepository;
import com.lib.libmansys.repository.PublisherRepository;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;
    private final GenreRepository genreRepository;
    @Autowired
    public BookService(BookRepository bookRepository, AuthorRepository authorRepository, PublisherRepository publisherRepository, GenreRepository genreRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.publisherRepository = publisherRepository;
        this.genreRepository = genreRepository;
    }


    public Book addBook(CreateBookRequest request) {
        Book book = new Book();
        book.setTitle(request.getTitle());

        List<Author> authors = request.getAuthors().stream()
                .map(authorFullName -> {
                    if (authorFullName.trim().isEmpty()) {
                        throw new IllegalArgumentException("Author name cannot be empty.");
                    }
                    String[] names = authorFullName.split(" ");
                    String firstName = names.length > 0 ? names[0] : "";
                    String lastName = names.length > 1 ? names[1] : "DefaultSurname"; // Varsayılan soyadı kullanın veya hata fırlatın
                    return authorRepository.findByFirstNameAndLastName(firstName, lastName)
                            .orElseGet(() -> authorRepository.save(new Author(firstName, lastName)));
                })
                .collect(Collectors.toList());
        book.setAuthors(authors);

        // Publishers handling
        List<Publisher> publishers = request.getPublishers().stream()
                .map(publisherName -> publisherRepository.findByName(publisherName)
                        .orElseGet(() -> publisherRepository.save(new Publisher(publisherName))))
                .collect(Collectors.toList());
        book.setPublishers(publishers);

        // Genres handling
        List<Genre> genres = request.getGenres().stream()
                .map(genreName -> genreRepository.findByName(genreName)
                        .orElseGet(() -> genreRepository.save(new Genre(genreName))))
                .collect(Collectors.toList());
        book.setGenres(genres);

        String fileName = StringUtils.cleanPath(request.getFile().getOriginalFilename());
        if (fileName.contains("..")) {
            System.out.println("Geçersiz dosya");
        }
        try {
            book.setBase64image(Base64.getEncoder().encodeToString(request.getFile().getBytes()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        book.setExplanation(request.getExplanation());
        book.setStatus(BookStatus.AVAILABLE);

        return bookRepository.save(book);
    }



    public void deleteBook(Long id) {

        bookRepository.deleteById(id);

    }

    public Book findBooksById(Long id) {
        return bookRepository.findById(id).orElse(null);
    }
    @Transactional
    public Page<Book> findBooksByStatus(BookStatus status, Pageable pageable) {
        return bookRepository.findByStatus(status, pageable);
    }
    @Transactional
    public List<Book> findBooksByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }

    @Transactional
    public Page<Book> findBooksByFilters(String author, String genre, String publisher, String title, Pageable pageable) {
        if (author != null && genre == null && publisher == null && title == null) {
            return bookRepository.findByAuthorsFirstNameContainingIgnoreCase(author, pageable);
        } else if (author == null && genre != null && publisher == null && title == null) {
            return bookRepository.findByGenresNameContainingIgnoreCase(genre, pageable);
        } else if (author == null && genre == null && publisher != null && title == null) {
            return bookRepository.findByPublishersNameContainingIgnoreCase(publisher, pageable);
        } else if (author == null && genre == null && publisher == null && title != null) {
            return bookRepository.findByTitleContainingIgnoreCase(title, pageable);
        } else {
            return bookRepository.findAll(pageable); // Fallback to returning all books with pagination
        }
    }

    public void exportBooks(HttpServletResponse response) throws IOException {
        List<Book> books = bookRepository.findAll();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Books");
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Title");
        headerRow.createCell(2).setCellValue("Status");
        int rowNum = 1;
        for (Book book : books) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(book.getId());
            row.createCell(1).setCellValue(book.getTitle());
            row.createCell(2).setCellValue(book.getStatus().toString());
        }
        for(int i = 0; i < 3; i++) {
            sheet.autoSizeColumn(i);
        }
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=books.xlsx");
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }


    public Page<Book> findAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    //    public Book updateBook(Long id, Book updatedBook) {
//        return bookRepository.findById(id)
//                .map(book -> {
//                    book.setTitle(updatedBook.getTitle());
//                    book.setAuthors(updatedBook.getAuthors());
//                    book.setPublishers(updatedBook.getPublishers());
//                    book.setGenres(updatedBook.getGenres());
//
//                    String fileName = StringUtils.cleanPath(updatedBook.getFile().getOriginalFilename());
//                    if (fileName.contains("..")) {
//                        System.out.println("Geçersiz dosya");
//                    }
//                    try {
//                        book.setBase64image(Base64.getEncoder().encodeToString(createBookRequest.getFile().getBytes()));
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                    return bookRepository.save(book);
//                })
//                .orElseThrow(() -> new RuntimeException(id +" numaralı kitap bulunamadı. "));
//    }
}

