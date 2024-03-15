package com.bitcoderdotcom.librarymanagementsystem.service;

import com.bitcoderdotcom.librarymanagementsystem.dto.ApiResponse;
import com.bitcoderdotcom.librarymanagementsystem.dto.LibrarianDto;
import com.bitcoderdotcom.librarymanagementsystem.entities.Librarian;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;

public interface LibrarianService {

    ResponseEntity<ApiResponse<LibrarianDto.Response>> getLibrarianById(String id);
    ResponseEntity<ApiResponse<List<LibrarianDto.Response>>> getAllLibrarians();
    ResponseEntity<ApiResponse<String>> removeLibrarian(String id, String email);
    ResponseEntity<ApiResponse<LibrarianDto.Response>> updateLibrarianDetails(LibrarianDto librarianDto, Principal principal);
}
