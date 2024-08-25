package com.lib.libmansys.dto;


import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@Data
public class UpdateBookRequest {
    private String title;
    private String explanation;
    private List<Long> authorIds;
    private List<Long> publisherIds;
    private List<Long> genreIds;
    private MultipartFile file;

}