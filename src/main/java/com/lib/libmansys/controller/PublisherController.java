package com.lib.libmansys.controller;

import com.lib.libmansys.dto.CreatePublisherInput;
import com.lib.libmansys.entity.Genre;
import com.lib.libmansys.service.PublisherService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.lib.libmansys.entity.Publisher;
import java.util.List;

@RestController
@RequestMapping("/api/publishers")
@RequiredArgsConstructor
public class PublisherController {

    private PublisherService publisherService;

    @GetMapping("/getAll")
    public ResponseEntity<List<Publisher>> getAllPublishers() {
        return ResponseEntity.ok(publisherService.getAllPublishers());
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<Publisher> getPublisherById(@PathVariable Long id) {
        Publisher publisher = publisherService.getPublisherById(id);
        return publisher != null ? ResponseEntity.ok(publisher) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<Publisher> createPublisher(@RequestBody CreatePublisherInput createPublisherInput) {
        Publisher savedPublisher = publisherService.createPublisher(createPublisherInput);
        return ResponseEntity.ok(savedPublisher);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<Publisher> updateGenre(@PathVariable Long id, @RequestBody Publisher publisher) {
        Publisher updatedPublisher = publisherService.updatePublisher(id, publisher);
        return updatedPublisher != null ? ResponseEntity.ok(updatedPublisher) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletePublisher(@PathVariable Long id) {
        boolean deleted = publisherService.deletePublisher(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
