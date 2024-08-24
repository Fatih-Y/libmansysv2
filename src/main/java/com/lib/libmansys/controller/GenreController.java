package com.lib.libmansys.controller;

import com.lib.libmansys.dto.CreateGenreInput;
import com.lib.libmansys.entity.Genre;
import com.lib.libmansys.service.GenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/genres")
public class GenreController {

    private final GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }


    @PostMapping("/add")
    public ResponseEntity<Genre> addGenre(@RequestBody CreateGenreInput createGenreInput) {
        Genre savedGenre = genreService.createGenre(createGenreInput);
        return ResponseEntity.ok(savedGenre);
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<Genre> updateGenre(@PathVariable Long id, @RequestBody Genre genre) {
        Genre updatedGenre = genreService.updateGenre(id, genre);
        return ResponseEntity.ok(updatedGenre);
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteGenre(@PathVariable Long id) {
        genreService.deleteGenre(id);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/getAll")
    public ResponseEntity<List<Genre>> getAllGenres() {
        List<Genre> genres = genreService.getAllGenres();
        return ResponseEntity.ok(genres);
    }


    @GetMapping("/getGenreById/{id}")
    public ResponseEntity<Genre> getGenreById(@PathVariable Long id) {
        Optional<Genre> genre = genreService.getGenreById(id);
        return genre.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
