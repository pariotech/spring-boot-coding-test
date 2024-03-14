package com.bitcoderdotcom.librarymanagementsystem.dto;

import com.bitcoderdotcom.librarymanagementsystem.constant.Genre;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {

    private String title;
    private String author;
    private String ISBN;
    private Genre genre;
    private long quantity;
    private String userId;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {

        private Long id;
        private String title;
        private String author;
        private String ISBN;
        private Genre genre;
        private long quantity;
        private String userId;
    }
}

