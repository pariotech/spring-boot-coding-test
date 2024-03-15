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
    private String isbn;
    private Genre genre;
    private long quantity;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {

        private String id;
        private String title;
        private String author;
        private String isbn;
        private Genre genre;
        private long quantity;
    }
}


