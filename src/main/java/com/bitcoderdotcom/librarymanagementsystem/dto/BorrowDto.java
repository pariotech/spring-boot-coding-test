package com.bitcoderdotcom.librarymanagementsystem.dto;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BorrowDto {

    private String memberId;
    private String bookId;
    private LocalDateTime borrowedAt;
    private LocalDateTime returnedAt;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {

        private String borrowId;
        private String memberId;
        private String bookId;
        private LocalDateTime borrowedAt;
        private LocalDateTime returnedAt;
    }
}


