package com.bitcoderdotcom.librarymanagementsystem.service;

import com.bitcoderdotcom.librarymanagementsystem.constant.Genre;
import com.bitcoderdotcom.librarymanagementsystem.dto.ApiResponse;
import com.bitcoderdotcom.librarymanagementsystem.dto.BookDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;

public interface BookService {

    ResponseEntity<ApiResponse<BookDto.Response>> insertBookIntoShelve(BookDto bookDto, Principal principal);
    ResponseEntity<ApiResponse<BookDto.Response>> getBookById(String id);
    ResponseEntity<ApiResponse<Page<BookDto.Response>>> getAllBooks(Principal principal, Pageable pageable);
    ResponseEntity<ApiResponse<List<BookDto.Response>>> searchBooks(String title, String author, Genre genre);
    ResponseEntity<ApiResponse<BookDto.Response>> updateBookDetails(String bookId, BookDto bookDto, Principal principal);
    ResponseEntity<ApiResponse<List<BookDto.Response>>> getBooksByLibrarianId(String librarianId, Principal principal);
    ResponseEntity<ApiResponse<String>> removeBookFromShelve(String id, Principal principal);
}
