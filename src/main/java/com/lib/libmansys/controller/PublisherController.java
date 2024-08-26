package com.lib.libmansys.controller;

import com.lib.libmansys.dto.CreatePublisherInput;
import com.lib.libmansys.service.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.lib.libmansys.entity.Publisher;
import java.util.List;

@RestController
@RequestMapping("/api/publishers")
public class PublisherController {

    @Autowired
    private PublisherService publisherService;

    @GetMapping("/getAll")
    public ResponseEntity<List<Publisher>> getAllPublishers() {
        return ResponseEntity.ok(publisherService.getAllPublishers());
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<Publisher> getPublisherById(@PathVariable Long id) {
        return ResponseEntity.ok(publisherService.getPublisherById(id));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<Publisher> createPublisher(@RequestBody CreatePublisherInput createPublisherInput) {
        Publisher savedPublisher = publisherService.createPublisher(createPublisherInput);
        return ResponseEntity.ok(savedPublisher);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<Publisher> updatePublisher(@PathVariable Long id, @RequestBody Publisher publisher) {
        Publisher existingPublisher = publisherService.getPublisherById(id);
        existingPublisher.setName(publisher.getName());
        existingPublisher.setBooks(publisher.getBooks());
        publisherService.createPublisher(new CreatePublisherInput());
        return ResponseEntity.ok(existingPublisher);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deletePublisher(@PathVariable Long id) {
        publisherService.deletePublisher(id);
        return ResponseEntity.ok("Publisher deleted successfully.");
    }
}
