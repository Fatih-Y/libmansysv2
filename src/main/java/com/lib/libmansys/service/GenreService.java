package com.lib.libmansys.service;

import com.lib.libmansys.dto.CreateGenreInput;
import com.lib.libmansys.entity.Genre;
import com.lib.libmansys.repository.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GenreService {

    private final GenreRepository genreRepository;

    @Autowired
    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }

    public Optional<Genre> getGenreById(Long id) {
        return genreRepository.findById(id);
    }

    public Genre createGenre(CreateGenreInput createGenreInput) {
        Genre genre = new Genre();
        genre.setName(createGenreInput.getName());
        return genreRepository.save(genre);
    }

    public Genre updateGenre(Long id, Genre updatedGenre) {
        Optional<Genre> existingGenreOptional = genreRepository.findById(id);
        if (existingGenreOptional.isPresent()) {
            Genre existingGenre = existingGenreOptional.get();
            existingGenre.setName(updatedGenre.getName());
            return genreRepository.save(existingGenre);
        }
        return null;
    }

    public void deleteGenre(Long id) {
        genreRepository.deleteById(id);
    }
}





