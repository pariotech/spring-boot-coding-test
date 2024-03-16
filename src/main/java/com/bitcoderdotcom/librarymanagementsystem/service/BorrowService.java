package com.bitcoderdotcom.librarymanagementsystem.service;

import com.bitcoderdotcom.librarymanagementsystem.dto.ApiResponse;
import com.bitcoderdotcom.librarymanagementsystem.dto.BorrowDto;
import com.bitcoderdotcom.librarymanagementsystem.dto.BorrowRequestDto;
import com.bitcoderdotcom.librarymanagementsystem.dto.ReturnRequestDto;
import io.undertow.util.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.concurrent.CompletableFuture;

public interface BorrowService {

    ResponseEntity<ApiResponse<BorrowDto.Response>> borrowBook(BorrowRequestDto borrowRequestDto, Principal principal);
    ResponseEntity<ApiResponse<BorrowDto.Response>> returnBook(ReturnRequestDto requestDto, Principal principal);
    ResponseEntity<ApiResponse<Page<BorrowDto.Response>>> trackBorrowedBooks(Principal principal, Pageable pageable);
}
