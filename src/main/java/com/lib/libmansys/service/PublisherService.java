package com.lib.libmansys.service;

import com.lib.libmansys.dto.CreatePublisherInput;
import com.lib.libmansys.entity.Publisher;
import com.lib.libmansys.repository.PublisherRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PublisherService {

    private final PublisherRepository publisherRepository;

    public List<Publisher> getAllPublishers() {
        return publisherRepository.findAll();
    }

    public Publisher getPublisherById(Long id) {
        return publisherRepository.findById(id).orElse(null); // checked
    }

    public Publisher createPublisher(CreatePublisherInput createPublisherInput) {
        Publisher publisher = new Publisher();
        publisher.setName(createPublisherInput.getName());
        return publisherRepository.save(publisher);
    }

    public Publisher updatePublisher(Long id, Publisher updatedPublisher) {
        Optional<Publisher> existingPublisherOptional = publisherRepository.findById(id);
        if (existingPublisherOptional.isPresent()) {
            Publisher existingPublisher = existingPublisherOptional.get();
            existingPublisher.setName(updatedPublisher.getName());
            return publisherRepository.save(existingPublisher);
        }
        return null; // checked
    }

    public boolean deletePublisher(Long id) {
        return publisherRepository.findById(id)
                .map(book -> {
                    publisherRepository.delete(book);
                    return true;
                })
                .orElse(false);
    }
}
