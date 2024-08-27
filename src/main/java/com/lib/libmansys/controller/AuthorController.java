package com.lib.libmansys.controller;

import com.lib.libmansys.dto.Author.CreateAuthorInput;
import com.lib.libmansys.entity.Author;
import com.lib.libmansys.service.AuthorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/authors")
public class AuthorController {

    private final AuthorService authorService;

    @Autowired
    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @Operation(tags = "Author", description = "Create a new author", responses = {
            @ApiResponse(description = "Success", responseCode = "200"

            ), @ApiResponse(description = "Data Not Found", responseCode = "404"

    )})
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/addAuthor")
    public ResponseEntity<Author> addAuthor(@RequestBody CreateAuthorInput createAuthorInput) {
        Author savedAuthor = authorService.createAuthor(createAuthorInput);
        return ResponseEntity.ok(savedAuthor);
    }

    @Operation(tags = "Author", description = "Update existing author", responses = {
            @ApiResponse(description = "Success", responseCode = "200"

            ), @ApiResponse(description = "Data Not Found", responseCode = "404"

    )})
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<Author> updateAuthor(@PathVariable Long id, @RequestBody Author author) {
        Author updatedAuthor = authorService.updateAuthor(id, author);
        return ResponseEntity.ok(updatedAuthor);
    }
    @Operation(tags = "Author", description = "Delete author", responses = {
            @ApiResponse(description = "Success", responseCode = "200"

            ), @ApiResponse(description = "Data Not Found", responseCode = "404"

    )})
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.ok().build();
    }

    @Operation(tags = "Author", description = "Get all the authors", responses = {
            @ApiResponse(description = "Success", responseCode = "200"

            ), @ApiResponse(description = "Data Not Found", responseCode = "404"

    )})
    @GetMapping("/getAllAuthors")
    public ResponseEntity<List<Author>> getAllAuthors() {
        List<Author> authors = authorService.getAllAuthors();
        return ResponseEntity.ok(authors);
    }

    @Operation(tags = "Author", description = "Get a single author's information by their id", responses = {
            @ApiResponse(description = "Success", responseCode = "200"

            ), @ApiResponse(description = "Data Not Found", responseCode = "404"

    )})
    @GetMapping("/search")
    public ResponseEntity<Author> getAuthorById(@PathVariable Long id) {
        Optional<Author> author = authorService.getAuthorById(id);
        return author.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}


