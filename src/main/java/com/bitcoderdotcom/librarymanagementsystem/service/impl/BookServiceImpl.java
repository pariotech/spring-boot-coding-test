package com.bitcoderdotcom.librarymanagementsystem.service.impl;

import com.bitcoderdotcom.librarymanagementsystem.constant.Genre;
import com.bitcoderdotcom.librarymanagementsystem.constant.Roles;
import com.bitcoderdotcom.librarymanagementsystem.dto.ApiResponse;
import com.bitcoderdotcom.librarymanagementsystem.dto.BookDto;
import com.bitcoderdotcom.librarymanagementsystem.entities.Book;
import com.bitcoderdotcom.librarymanagementsystem.entities.Borrow;
import com.bitcoderdotcom.librarymanagementsystem.entities.User;
import com.bitcoderdotcom.librarymanagementsystem.exception.BadRequestException;
import com.bitcoderdotcom.librarymanagementsystem.exception.ResourceNotFoundException;
import com.bitcoderdotcom.librarymanagementsystem.exception.UnauthorizedException;
import com.bitcoderdotcom.librarymanagementsystem.repository.BookRepository;
import com.bitcoderdotcom.librarymanagementsystem.repository.BorrowRepository;
import com.bitcoderdotcom.librarymanagementsystem.repository.UserRepository;
import com.bitcoderdotcom.librarymanagementsystem.service.BookService;
import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.beans.FeatureDescriptor;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@AllArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BorrowRepository borrowRepository;

    @Override
    public ResponseEntity<ApiResponse<BookDto.Response>> insertBookIntoShelve(BookDto bookDto, Principal principal) {
        log.info("Inserting book with title: {}", bookDto.getTitle());
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", principal.getName()));
        if (user.getRoles() != Roles.LIBRARIAN) {
            throw new UnauthorizedException("Only a Librarian can insert a book");
        }
        Book book = convertDtoToEntity(bookDto, user);
        bookRepository.save(book);
        BookDto.Response bookResponse = convertEntityToDto(book);
        ApiResponse<BookDto.Response> apiResponse = new ApiResponse<>(
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                true,
                "Book inserted successfully to "+ bookDto.getGenre()+ " shelve by "+ user.getName(),
                bookResponse
        );
        log.info("Book inserted successfully with title: {}", bookDto.getTitle());
        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public ResponseEntity<ApiResponse<BookDto.Response>> getBookById(String id) {
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
    public ResponseEntity<ApiResponse<BookDto.Response>> updateBookDetails(String bookId, BookDto bookDto, Principal principal) {
        log.info("Updating book details for book id: {}", bookId);
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", principal.getName()));
        if (user.getRoles() != Roles.LIBRARIAN) {
            throw new UnauthorizedException("Only a Librarian can update book details");
        }
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", bookId));
        BeanUtils.copyProperties(bookDto, book, getNullPropertyNames(bookDto));
        bookRepository.save(book);
        BookDto.Response bookResponse = convertEntityToDto(book);
        ApiResponse<BookDto.Response> apiResponse = new ApiResponse<>(
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                true,
                "Book details updated successfully",
                bookResponse
        );
        log.info("Book details updated successfully for book id: {}", bookId);
        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public ResponseEntity<ApiResponse<List<BookDto.Response>>> getBooksByLibrarianId(String librarianId, Principal principal) {
        log.info("Fetching all books inserted by librarian with id: {}", librarianId);
        User user = userRepository.findById(librarianId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", librarianId));
        if (user.getRoles() != Roles.LIBRARIAN) {
            throw new UnauthorizedException("Only a Librarian can insert a book");
        }
        List<Book> books = bookRepository.findByUser(user);
        List<BookDto.Response> bookResponses = books.stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList());
        ApiResponse<List<BookDto.Response>> apiResponse = new ApiResponse<>(
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                true,
                "Books inserted by librarian with id " + librarianId + " fetched successfully",
                bookResponses
        );
        log.info("Books inserted by librarian with id {} fetched successfully", librarianId);
        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public ResponseEntity<ApiResponse<String>> removeBookFromShelve(String id, Principal principal) {
        log.info("Removing book with id: {}", id);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", principal.getName()));
        // Check if the user is a LIBRARIAN
        if (user.getRoles() != Roles.LIBRARIAN) {
            throw new UnauthorizedException("Only a LIBRARIAN can remove a book");
        }

        // Check if the book has been returned
        List<Borrow> borrows = borrowRepository.findByBookId(id);
        for (Borrow borrow : borrows) {
            if (borrow.getReturnedAt() == null) {
                throw new BadRequestException("The book has not been returned yet");
            }
        }

        // Delete the Borrow records
        for (Borrow borrow : borrows) {
            borrowRepository.delete(borrow);
        }

        bookRepository.delete(book);

        ApiResponse<String> apiResponse = new ApiResponse<>(
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                true,
                "Book removed successfully",
                "Book with id " + id + " removed by "+ user.getName() + "."
        );
        log.info("Book removed successfully with id: {}", id);
        return ResponseEntity.ok(apiResponse);
    }


    private BookDto.Response convertEntityToDto(Book book) {
        BookDto.Response bookResponse = new BookDto.Response();
        bookResponse.setId(book.getId());
        bookResponse.setTitle(book.getTitle());
        bookResponse.setAuthor(book.getAuthor());
        bookResponse.setIsbn(book.getIsbn());
        bookResponse.setGenre(book.getGenre());
        bookResponse.setQuantity(book.getQuantity());
        return bookResponse;
    }


    private Book convertDtoToEntity(BookDto bookDto, User user) {
        Book book = new Book();
        book.setTitle(bookDto.getTitle());
        book.setAuthor(bookDto.getAuthor());
        book.setIsbn(bookDto.getIsbn());
        book.setGenre(bookDto.getGenre());
        book.setQuantity(bookDto.getQuantity());
        book.setUser(user);
        return book;
    }

    private String[] getNullPropertyNames(Object source) {
        final BeanWrapper wrappedSource = new BeanWrapperImpl(source);
        return Stream.of(wrappedSource.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null)
                .toArray(String[]::new);
    }
}
