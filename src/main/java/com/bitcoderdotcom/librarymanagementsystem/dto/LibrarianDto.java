package com.bitcoderdotcom.librarymanagementsystem.dto;

import com.bitcoderdotcom.librarymanagementsystem.constant.Roles;
import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LibrarianDto {

    private String name;
    private String email;
    private String password;
    private Roles roles;
    private List<Long> bookIds;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {

        private Long id;
        private String name;
        private String email;
        private Roles roles;
        private List<Long> bookIds;
    }
}
