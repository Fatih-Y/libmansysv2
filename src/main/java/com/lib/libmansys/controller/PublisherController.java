package com.lib.libmansys.controller;

import com.lib.libmansys.dto.CreatePublisherInput;
import com.lib.libmansys.service.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.lib.libmansys.entity.Publisher;
import java.util.List;

@RestController
@RequestMapping("/api/publishers")
public class PublisherController {

    @Autowired
    private PublisherService publisherService;

    @GetMapping
    public ResponseEntity<List<Publisher>> getAllPublishers() {
        return ResponseEntity.ok(publisherService.getAllPublishers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Publisher> getPublisherById(@PathVariable Long id) {
        return ResponseEntity.ok(publisherService.getPublisherById(id));
    }

    @PostMapping
    public ResponseEntity<Publisher> createPublisher(@RequestBody CreatePublisherInput createPublisherInput) {
        Publisher savedPublisher = publisherService.createPublisher(createPublisherInput);
        return ResponseEntity.ok(savedPublisher);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Publisher> updatePublisher(@PathVariable Long id, @RequestBody Publisher publisher) {
        Publisher existingPublisher = publisherService.getPublisherById(id);
        existingPublisher.setName(publisher.getName());
        existingPublisher.setBooks(publisher.getBooks());
        publisherService.createPublisher(new CreatePublisherInput());
        return ResponseEntity.ok(existingPublisher);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePublisher(@PathVariable Long id) {
        publisherService.deletePublisher(id);
        return ResponseEntity.ok("Publisher deleted successfully.");
    }
}
