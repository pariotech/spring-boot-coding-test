package com.bitcoderdotcom.librarymanagementsystem.service;

import com.bitcoderdotcom.librarymanagementsystem.dto.ApiResponse;
import com.bitcoderdotcom.librarymanagementsystem.dto.LibrarianDto;
import com.bitcoderdotcom.librarymanagementsystem.entities.Librarian;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface LibrarianService {

    ResponseEntity<ApiResponse<LibrarianDto.Response>> getLibrarianById(Long id);
    ResponseEntity<ApiResponse<List<LibrarianDto.Response>>> getAllLibrarians();
    ResponseEntity<ApiResponse<String>> removeLibrarian(Long id, String email);
}
