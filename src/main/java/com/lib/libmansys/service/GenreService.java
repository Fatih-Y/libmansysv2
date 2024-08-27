package com.lib.libmansys.service;

import com.lib.libmansys.dto.CreateGenreInput;
import com.lib.libmansys.entity.Genre;
import com.lib.libmansys.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository genreRepository;

    public List<Genre> getAllGenres() {
        List<Genre> genres = genreRepository.findAll();
        return genres.isEmpty() ? Collections.emptyList() : genres;
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
        return null;  // returning null values should be handled by controller!
    }

    public void deleteGenre(Long id) {
        Optional<Genre> genre = genreRepository.findById(id);
        if(genre.isPresent()) {
            genreRepository.deleteById(id); // todo: check if its necessary because it returns void
        } else {
            throw new RuntimeException(id+ "id'ye sahip tür bulunamadı.");
        }
    }
}





