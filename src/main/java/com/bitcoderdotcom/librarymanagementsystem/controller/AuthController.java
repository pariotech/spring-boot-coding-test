package com.bitcoderdotcom.librarymanagementsystem.controller;

import com.bitcoderdotcom.librarymanagementsystem.dto.ApiResponse;
import com.bitcoderdotcom.librarymanagementsystem.dto.SignInRequest;
import com.bitcoderdotcom.librarymanagementsystem.dto.UserRegistrationRequest;
import com.bitcoderdotcom.librarymanagementsystem.security.services.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
@RequestMapping("/lms/v1/auth")
public class AuthController {

    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserRegistrationRequest.Response>> register(@RequestBody UserRegistrationRequest request) {
        return authService.register(request);
    }

    @PostMapping("/signIn")
    public ResponseEntity<ApiResponse<SignInRequest.Response>> signIn(@RequestBody SignInRequest request) {
        return authService.signIn(request);
    }
}