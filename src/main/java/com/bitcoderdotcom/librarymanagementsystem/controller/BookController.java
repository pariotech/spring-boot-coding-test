package com.bitcoderdotcom.librarymanagementsystem.controller;

import com.bitcoderdotcom.librarymanagementsystem.constant.Genre;
import com.bitcoderdotcom.librarymanagementsystem.dto.ApiResponse;
import com.bitcoderdotcom.librarymanagementsystem.dto.BookDto;
import com.bitcoderdotcom.librarymanagementsystem.service.BookService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/lms/v1/book")
@AllArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<BookDto.Response>> insertBookIntoShelve(@RequestBody BookDto bookDto, Principal principal) {
        return bookService.insertBookIntoShelve(bookDto, principal);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookDto.Response>> getBookById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<BookDto.Response>>> getAllBooks(Principal principal) {
        return bookService.getAllBooks(principal);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<BookDto.Response>>> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) Genre genre) {
        return bookService.searchBooks(title, author, genre);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> removeBookFromShelve(@PathVariable Long id, Principal principal) {
        return bookService.removeBookFromShelve(id, principal);
    }
}
