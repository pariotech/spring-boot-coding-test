package com.bitcoderdotcom.librarymanagementsystem.controller;

import com.bitcoderdotcom.librarymanagementsystem.dto.ApiResponse;
import com.bitcoderdotcom.librarymanagementsystem.dto.LibrarianDto;
import com.bitcoderdotcom.librarymanagementsystem.entities.Librarian;
import com.bitcoderdotcom.librarymanagementsystem.service.LibrarianService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lms/v1/librarian")
@AllArgsConstructor
public class LibrarianController {

    private final LibrarianService librarianService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LibrarianDto.Response>> getLibrarianById(@PathVariable Long id) {
        return librarianService.getLibrarianById(id);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<LibrarianDto.Response>>> getAllLibrarians() {
        return librarianService.getAllLibrarians();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> removeLibrarian(@PathVariable Long id, @RequestHeader("email") String email) {
        return librarianService.removeLibrarian(id, email);
    }
}
