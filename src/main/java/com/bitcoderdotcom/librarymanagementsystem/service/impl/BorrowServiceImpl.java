package com.bitcoderdotcom.librarymanagementsystem.service.impl;

import com.bitcoderdotcom.librarymanagementsystem.constant.Roles;
import com.bitcoderdotcom.librarymanagementsystem.dto.ApiResponse;
import com.bitcoderdotcom.librarymanagementsystem.dto.BorrowDto;
import com.bitcoderdotcom.librarymanagementsystem.dto.BorrowRequestDto;
import com.bitcoderdotcom.librarymanagementsystem.dto.ReturnRequestDto;
import com.bitcoderdotcom.librarymanagementsystem.entities.Book;
import com.bitcoderdotcom.librarymanagementsystem.entities.Borrow;
import com.bitcoderdotcom.librarymanagementsystem.entities.Member;
import com.bitcoderdotcom.librarymanagementsystem.entities.User;
import com.bitcoderdotcom.librarymanagementsystem.exception.BadRequestException;
import com.bitcoderdotcom.librarymanagementsystem.exception.ResourceNotFoundException;
import com.bitcoderdotcom.librarymanagementsystem.exception.UnauthorizedException;
import com.bitcoderdotcom.librarymanagementsystem.repository.BookRepository;
import com.bitcoderdotcom.librarymanagementsystem.repository.BorrowRepository;
import com.bitcoderdotcom.librarymanagementsystem.repository.UserRepository;
import com.bitcoderdotcom.librarymanagementsystem.service.BorrowService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

@Service
@Slf4j
@AllArgsConstructor
public class BorrowServiceImpl implements BorrowService {

    private final BorrowRepository borrowRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<BorrowDto.Response>> borrowBook(BorrowRequestDto borrowRequestDto, Principal principal) {
        String bookId = borrowRequestDto.getBookId();
        log.info("Attempting to borrow book with ID: {}", bookId);
        Member member = (Member) userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", principal.getName()));
        log.info("Found member with email: {}", principal.getName());
        if (member.getRoles() != Roles.MEMBER) {
            throw new UnauthorizedException("Only a Member can borrow a book");
        }
        log.info("Member has correct role");
        if (member.isWithBorrowedBook()) {
            try {
                throw new BadRequestException("You must return the borrowed book before borrowing another one");
            } catch (BadRequestException e) {
                throw new RuntimeException(e);
            }
        }
        log.info("Member has no borrowed books");
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", bookId));
        log.info("Found book with ID: {}", bookId);

        if (book.getQuantity() <= 0) {
            try {
                throw new BadRequestException("This book is currently out of stock");
            } catch (BadRequestException e) {
                throw new RuntimeException(e);
            }
        }
        log.info("Book is in stock");

        book.setQuantity(book.getQuantity() - 1);
        log.info("Updated book quantity");

        Borrow borrow = new Borrow(member, book);
        log.info("Created new borrow record");

        borrowRepository.save(borrow);
        log.info("Borrow record has been saved in the repository");
        BorrowDto.Response borrowResponse = convertEntityToDto(borrow);
        member.setWithBorrowedBook(true);
        userRepository.save(member);
        ApiResponse<BorrowDto.Response> apiResponse = new ApiResponse<>(
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                true,
                "Book borrowed successfully by " + member.getName(),
                borrowResponse
        );
        log.info("Book with ID: {} has been borrowed successfully", bookId);
        return ResponseEntity.ok(apiResponse);
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<BorrowDto.Response>> returnBook(ReturnRequestDto requestDto, Principal principal) {
        String borrowId = requestDto.getBorrowId();
        log.info("Attempting to return book with borrow ID: {}", borrowId);
        Member member = (Member) userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", principal.getName()));
        if (member.getRoles() != Roles.MEMBER) {
            throw new UnauthorizedException("Only a Member can return a book");
        }
        Borrow borrow = borrowRepository.findById(borrowId)
                .orElseThrow(() -> new ResourceNotFoundException("Borrow", "id", borrowId));
        if (!borrow.getMember().getId().equals(member.getId())) {
            throw new UnauthorizedException("You can only return books that you have borrowed");
        }
        if (borrow.getReturnedAt() != null) {
            try {
                throw new BadRequestException("This book has already been returned");
            } catch (BadRequestException e) {
                throw new RuntimeException(e);
            }
        }
        borrow.setReturnedAt(LocalDateTime.now());
        borrowRepository.save(borrow);
        log.info("Borrow record with ID: {} has been updated in the repository", borrowId);

        member.setWithBorrowedBook(false);
        userRepository.save(member);

        Book book = borrow.getBook();
        book.setQuantity(book.getQuantity() + 1);
        bookRepository.save(book);
        log.info("Book with ID: {} has been updated in the repository", book.getId());
        BorrowDto.Response borrowResponse = convertEntityToDto(borrow);
        ApiResponse<BorrowDto.Response> apiResponse = new ApiResponse<>(
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                true,
                "Book returned successfully by " + member.getName(),
                borrowResponse
        );
        log.info("Book with borrow ID: {} has been returned successfully", borrowId);
        return ResponseEntity.ok(apiResponse);
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<Page<BorrowDto.Response>>> trackBorrowedBooks(Principal principal, Pageable pageable) {
        log.info("Attempting to track borrowed books");
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", principal.getName()));
        if (user.getRoles() != Roles.LIBRARIAN) {
            throw new UnauthorizedException("Only a Librarian can track borrowed books");
        }
        Page<Borrow> borrows = borrowRepository.findAll(pageable);
        Page<BorrowDto.Response> borrowResponses = borrows.map(this::convertEntityToDto);
        ApiResponse<Page<BorrowDto.Response>> apiResponse = new ApiResponse<>(
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                true,
                "Borrowed books tracked successfully",
                borrowResponses
        );
        log.info("Borrowed books have been tracked successfully");
        return ResponseEntity.ok(apiResponse);
    }

    private BorrowDto.Response convertEntityToDto(Borrow borrow) {
        BorrowDto.Response borrowResponse = new BorrowDto.Response();
        borrowResponse.setBorrowId(borrow.getId());
        borrowResponse.setMemberId(borrow.getMember().getId());
        borrowResponse.setBookId(borrow.getBook().getId());
        borrowResponse.setBorrowedAt(borrow.getBorrowedAt());
        borrowResponse.setReturnedAt(borrow.getReturnedAt());
        return borrowResponse;
    }
}
