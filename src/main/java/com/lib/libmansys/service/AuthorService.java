package com.lib.libmansys.service;


import com.lib.libmansys.dto.Author.CreateAuthorInput;
import com.lib.libmansys.entity.Author;
import com.lib.libmansys.repository.AuthorRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;


    public List<Author> getAllAuthors() {
        List<Author> authors = authorRepository.findAll();
        return authors.isEmpty() ? Collections.emptyList() : authors; // returning an empty list instead of null
    }

    public Optional<Author> getAuthorById(Long id) {
        return authorRepository.findById(id);
    }

    public Author createAuthor(CreateAuthorInput createAuthorInput) {
        Author author = new Author();
        author.setFirstName(createAuthorInput.getFirstName());
        author.setLastName(createAuthorInput.getLastName());
        return authorRepository.save(author);
    }

    public Author updateAuthor(Long id, Author updatedAuthor) {
        Optional<Author> existingAuthorOptional = authorRepository.findById(id);
        if (existingAuthorOptional.isPresent()) {
            Author existingAuthor = existingAuthorOptional.get();
            existingAuthor.setFirstName(updatedAuthor.getFirstName());
            existingAuthor.setLastName(updatedAuthor.getLastName());
            return authorRepository.save(existingAuthor);
        }
        throw new EntityNotFoundException(id+" id'ye sahip yazar bulunamadı.");
    }

    public boolean deleteAuthor(Long id) {
        return authorRepository.findById(id)
                .map(genre -> {
                    authorRepository.delete(genre);
                    return true;
                })
                .orElse(false);
    }
}
