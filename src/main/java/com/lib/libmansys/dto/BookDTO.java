package com.lib.libmansys.dto;

import lombok.Data;

@Data
public class BookDTO {
    private Long id;
    private String title;
    private String base64image;  // Include or exclude based on what the frontend needs
}
