package com.lib.libmansys.dto.Book;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@Data
public class CreateBookRequest {
    private String title;
    private String explanation;
    private List<String> authors;
    private List<String> publishers;
    private List<String> genres;
    private MultipartFile file;

}