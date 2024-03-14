package com.bitcoderdotcom.librarymanagementsystem.service;

import com.bitcoderdotcom.librarymanagementsystem.constant.Genre;
import com.bitcoderdotcom.librarymanagementsystem.dto.ApiResponse;
import com.bitcoderdotcom.librarymanagementsystem.dto.BookDto;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;

public interface BookService {

    ResponseEntity<ApiResponse<BookDto.Response>> insertBookIntoShelve(BookDto bookDto, Principal principal);
    ResponseEntity<ApiResponse<BookDto.Response>> getBookById(Long id);
    ResponseEntity<ApiResponse<List<BookDto.Response>>> getAllBooks(Principal principal);
    ResponseEntity<ApiResponse<List<BookDto.Response>>> searchBooks(String title, String author, Genre genre);
    ResponseEntity<ApiResponse<String>> removeBookFromShelve(Long id, Principal principal);
}
