package com.bitcoderdotcom.librarymanagementsystem.controller;

import com.bitcoderdotcom.librarymanagementsystem.dto.*;
import com.bitcoderdotcom.librarymanagementsystem.entities.Librarian;
import com.bitcoderdotcom.librarymanagementsystem.service.BookService;
import com.bitcoderdotcom.librarymanagementsystem.service.BorrowService;
import com.bitcoderdotcom.librarymanagementsystem.service.LibrarianService;
import com.bitcoderdotcom.librarymanagementsystem.service.MemberService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/lms/v1/librarian")
@AllArgsConstructor
public class LibrarianController {

    private final LibrarianService librarianService;
    private final MemberService memberService;
    private final BookService bookService;
    private final BorrowService borrowService;

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

    @GetMapping("/trackBorrowedBooks")
    public ResponseEntity<ApiResponse<Page<BorrowDto.Response>>> trackBorrowedBooks(Principal principal,
                                                                                    @RequestParam(defaultValue = "0") int page,
                                                                                    @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return borrowService.trackBorrowedBooks(principal, pageable);
    }

    @PatchMapping("/update-details")
    public ResponseEntity<ApiResponse<LibrarianDto.Response>> updateLibrarianDetails(@RequestBody LibrarianDto librarianDto, Principal principal) {
        return librarianService.updateLibrarianDetails(librarianDto, principal);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> removeLibrarian(@PathVariable String id, @RequestHeader("email") String email) {
        return librarianService.removeLibrarian(id, email);
    }

    @GetMapping("/members/all")
    public ResponseEntity<ApiResponse<Page<MemberDto.Response>>> getAllMembers(Principal principal,
                                                                               @RequestParam(defaultValue = "0") int page,
                                                                               @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return memberService.getAllMembers(principal, pageable);
    }
}
