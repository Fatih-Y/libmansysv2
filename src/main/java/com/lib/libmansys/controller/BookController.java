package com.lib.libmansys.controller;
import com.lib.libmansys.dto.Book.CreateBookRequest;
import com.lib.libmansys.entity.Book;
import com.lib.libmansys.entity.Enum.BookStatus;
import com.lib.libmansys.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;
    private static final Logger log = LoggerFactory.getLogger(BookController.class);


    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }
    @Operation(tags = "Book", description = "Add a new book. Creates a new author, publisher, genre if don't exist. ADMIN ONLY", responses = {
            @ApiResponse(description = "Success", responseCode = "200"

            ), @ApiResponse(description = "Data Not Found", responseCode = "404"

    )})
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value ="/addBook", consumes = {"multipart/form-data"})
    public ResponseEntity<String> addBook(@ModelAttribute CreateBookRequest createBookRequest) {
        bookService.addBook(createBookRequest);
        return ResponseEntity.ok("Kitap Ekleme Başarılı");
    }

    @Operation(tags = "Book", description = "Delete a book using it's id. ADMIN ONLY", responses = {
            @ApiResponse(description = "Success", responseCode = "200"

            ), @ApiResponse(description = "Data Not Found", responseCode = "404"

    )})
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok().build();
    }

    @Operation(tags = "Book", description = "Get a list of all the books using pagination.", responses = {
            @ApiResponse(description = "Success", responseCode = "200"

            ), @ApiResponse(description = "Data Not Found", responseCode = "404"

    )})
    @GetMapping("/getAllBooks")
    public Page<Book> getAllBooks(@PageableDefault(size = 10) Pageable pageable) {
        return bookService.findAllBooks(pageable);
    }
    @Operation(tags = "Book", responses = {
            @ApiResponse(description = "Success", responseCode = "200"

            ), @ApiResponse(description = "Data Not Found", responseCode = "404"

    )})
    @GetMapping("/getBookById/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        Book book = bookService.findBooksById(id);
        return book != null ? ResponseEntity.ok(book) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Operation(tags = "Book", responses = {
            @ApiResponse(description = "Success", responseCode = "200"

            ), @ApiResponse(description = "Data Not Found", responseCode = "404"

    )})
    @GetMapping("/getBooksByTitle")
    public ResponseEntity<List<Book>> getBooksByTitle(@RequestParam String title) {
        List<Book> books = bookService.findBooksByTitle(title);
        return ResponseEntity.ok(books);
    }

    @Operation(tags = "Book", responses = {
            @ApiResponse(description = "Success", responseCode = "200"

            ), @ApiResponse(description = "Data Not Found", responseCode = "404"

    )})
    @GetMapping("/search")
    public Page<Book> getBooksByFilters(@RequestParam(required = false) String author,
                                        @RequestParam(required = false) String genre,
                                        @RequestParam(required = false) String publisher,
                                        @RequestParam(required = false) String title,
                                        @PageableDefault(size = 10) Pageable pageable) {
        return bookService.findBooksByFilters(author, genre, publisher, title, pageable);
    }
    @Operation(tags = "Book", responses = {
            @ApiResponse(description = "Success", responseCode = "200"

            ), @ApiResponse(description = "Data Not Found", responseCode = "404"

    )})
    @GetMapping("/searchSingle")
    public Page<Book> searchBooks(@RequestParam(required = false) String searchTerm,
                                  @PageableDefault(size = 10) Pageable pageable) {
        return bookService.findBooksBySingleFilter(searchTerm, pageable);
    }


    @Operation(tags = "Book", responses = {
            @ApiResponse(description = "Success", responseCode = "200"

            ), @ApiResponse(description = "Data Not Found", responseCode = "404"

    )})
    @GetMapping("/getBooksByStatus")
    public Page<Book> getBooksByStatus(@RequestParam BookStatus status,
                                       @PageableDefault(size = 10) Pageable pageable) {
        return bookService.findBooksByStatus(status, pageable);
    }
    
    @Operation(tags = "Book", responses = {
            @ApiResponse(description = "Success", responseCode = "200"

            ), @ApiResponse(description = "Data Not Found", responseCode = "404"

    )})
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/export")
    public ResponseEntity<String> exportBooks(HttpServletResponse response) {
        try {
            bookService.exportBooks(response);
            return ResponseEntity.ok("Kitaplar başarıyla dışa aktarıldı.");
        } catch (IOException e) {
            log.error("Dosya dışa aktarma sırasında hata oluştu", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Dosya dışa aktarılamadı.");
        }
    }
}
