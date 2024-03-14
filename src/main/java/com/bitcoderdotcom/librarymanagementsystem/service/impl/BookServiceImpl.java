package com.bitcoderdotcom.librarymanagementsystem.service.impl;

import com.bitcoderdotcom.librarymanagementsystem.constant.Genre;
import com.bitcoderdotcom.librarymanagementsystem.constant.Roles;
import com.bitcoderdotcom.librarymanagementsystem.dto.ApiResponse;
import com.bitcoderdotcom.librarymanagementsystem.dto.BookDto;
import com.bitcoderdotcom.librarymanagementsystem.entities.Book;
import com.bitcoderdotcom.librarymanagementsystem.entities.User;
import com.bitcoderdotcom.librarymanagementsystem.exception.ResourceNotFoundException;
import com.bitcoderdotcom.librarymanagementsystem.exception.UnauthorizedException;
import com.bitcoderdotcom.librarymanagementsystem.repository.BookRepository;
import com.bitcoderdotcom.librarymanagementsystem.repository.UserRepository;
import com.bitcoderdotcom.librarymanagementsystem.service.BookService;
import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Override
    public ResponseEntity<ApiResponse<BookDto.Response>> insertBookIntoShelve(BookDto bookDto, Principal principal) {
        log.info("Creating book with title: {}", bookDto.getTitle());
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", principal.getName()));
        if (user.getRoles() != Roles.LIBRARIAN) {
            throw new UnauthorizedException("Only a Librarian can create a book");
        }
        Book book = convertDtoToEntity(bookDto, user);
        bookRepository.save(book);
        BookDto.Response bookResponse = convertEntityToDto(book);
        ApiResponse<BookDto.Response> apiResponse = new ApiResponse<>(
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                true,
                "Book created successfully",
                bookResponse
        );
        log.info("Book created successfully with title: {}", bookDto.getTitle());
        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public ResponseEntity<ApiResponse<BookDto.Response>> getBookById(Long id) {
        log.info("Fetching book with id: {}", id);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));
        BookDto.Response bookResponse = convertEntityToDto(book);
        ApiResponse<BookDto.Response> apiResponse = new ApiResponse<>(
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                true,
                "Book fetched successfully",
                bookResponse
        );
        log.info("Book fetched successfully with id: {}", id);
        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public ResponseEntity<ApiResponse<List<BookDto.Response>>> getAllBooks(Principal principal) {
        log.info("Fetching all books");
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", principal.getName()));
        if (user.getRoles() != Roles.LIBRARIAN) {
            throw new UnauthorizedException("Only a Librarian can fetch all books");
        }
        List<Book> books = bookRepository.findAll();
        List<BookDto.Response> bookResponses = books.stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList());
        ApiResponse<List<BookDto.Response>> apiResponse = new ApiResponse<>(
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                true,
                "All books fetched successfully",
                bookResponses
        );
        log.info("All books fetched successfully");
        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public ResponseEntity<ApiResponse<List<BookDto.Response>>> searchBooks(String title, String author, Genre genre) {
        log.info("Searching books with title: {}, author: {}, genre: {}", title, author, genre);
        List<Book> books = bookRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (title != null) {
                predicates.add(cb.equal(root.get("title"), title));
            }
            if (author != null) {
                predicates.add(cb.equal(root.get("author"), author));
            }
            if (genre != null) {
                predicates.add(cb.equal(root.get("genre"), genre));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        });
        List<BookDto.Response> bookResponses = books.stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList());
        ApiResponse<List<BookDto.Response>> apiResponse = new ApiResponse<>(
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                true,
                "Books searched successfully",
                bookResponses
        );
        log.info("Books searched successfully");
        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public ResponseEntity<ApiResponse<String>> removeBookFromShelve(Long id, Principal principal) {
        log.info("Removing book with id: {}", id);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));
        if (!book.getUser().getEmail().equals(principal.getName())) {
            throw new UnauthorizedException("You are not authorized to perform this operation");
        }
        bookRepository.delete(book);
        ApiResponse<String> apiResponse = new ApiResponse<>(
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                true,
                "Book removed successfully",
                "Book with id " + id + " removed."
        );
        log.info("Book removed successfully with id: {}", id);
        return ResponseEntity.ok(apiResponse);
    }

    private BookDto.Response convertEntityToDto(Book book) {
        BookDto.Response bookResponse = new BookDto.Response();
        bookResponse.setId(book.getId());
        bookResponse.setTitle(book.getTitle());
        bookResponse.setAuthor(book.getAuthor());
        bookResponse.setISBN(book.getISBN());
        bookResponse.setGenre(book.getGenre());
        bookResponse.setQuantity(book.getQuantity());
        bookResponse.setUserId(book.getUser().getId().toString());
        return bookResponse;
    }

    private Book convertDtoToEntity(BookDto bookDto, User user) {
        Book book = new Book();
        book.setTitle(bookDto.getTitle());
        book.setAuthor(bookDto.getAuthor());
        book.setISBN(bookDto.getISBN());
        book.setGenre(bookDto.getGenre());
        book.setQuantity(bookDto.getQuantity());
        book.setUser(user);
        return book;
    }
}
