package com.bitcoderdotcom.librarymanagementsystem.dto;

import com.bitcoderdotcom.librarymanagementsystem.constant.Roles;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class SignInRequest {

    @NotBlank(message = "email should not be blank")
    private String email;
    @NotBlank(message = "password should not be blank")
    private String password;

    @AllArgsConstructor
    @Getter
    @Setter
    public static class Response {
        private Long userId;
        private String token;
        private String type = "Bearer";
        private String name;
        private Set<Roles> roles;
        private LocalDateTime tokenExpirationDate;

        public Response(Long userId, String token, String name, Set<Roles> roles, LocalDateTime tokenExpirationDate) {
            this.userId = userId;
            this.token = token;
            this.name = name;
            this.roles = roles;
            this.tokenExpirationDate = tokenExpirationDate;
        }
    }
}
