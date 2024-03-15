package com.bitcoderdotcom.librarymanagementsystem.controller;

import com.bitcoderdotcom.librarymanagementsystem.dto.ApiResponse;
import com.bitcoderdotcom.librarymanagementsystem.dto.BookDto;
import com.bitcoderdotcom.librarymanagementsystem.dto.LibrarianDto;
import com.bitcoderdotcom.librarymanagementsystem.entities.Librarian;
import com.bitcoderdotcom.librarymanagementsystem.service.BookService;
import com.bitcoderdotcom.librarymanagementsystem.service.LibrarianService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/lms/v1/librarian")
@AllArgsConstructor
public class LibrarianController {

    private final LibrarianService librarianService;
    private final BookService bookService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LibrarianDto.Response>> getLibrarianById(@PathVariable String id) {
        return librarianService.getLibrarianById(id);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<LibrarianDto.Response>>> getAllLibrarians() {
        return librarianService.getAllLibrarians();
    }

    @GetMapping("books/{librarianId}")
    public ResponseEntity<ApiResponse<List<BookDto.Response>>> getBooksByLibrarianId(@PathVariable String librarianId, Principal principal) {
        return bookService.getBooksByLibrarianId(librarianId, principal);
    }

    @PutMapping("/update-details")
    public ResponseEntity<ApiResponse<LibrarianDto.Response>> updateLibrarianDetails(@RequestBody LibrarianDto librarianDto, Principal principal) {
        return librarianService.updateLibrarianDetails(librarianDto, principal);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> removeLibrarian(@PathVariable String id, @RequestHeader("email") String email) {
        return librarianService.removeLibrarian(id, email);
    }
}
