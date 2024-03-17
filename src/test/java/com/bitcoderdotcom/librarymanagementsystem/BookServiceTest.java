package com.bitcoderdotcom.librarymanagementsystem;


import com.bitcoderdotcom.librarymanagementsystem.constant.Genre;
import com.bitcoderdotcom.librarymanagementsystem.constant.Roles;
import com.bitcoderdotcom.librarymanagementsystem.dto.ApiResponse;
import com.bitcoderdotcom.librarymanagementsystem.dto.BookDto;
import com.bitcoderdotcom.librarymanagementsystem.entities.*;
import com.bitcoderdotcom.librarymanagementsystem.exception.BadRequestException;
import com.bitcoderdotcom.librarymanagementsystem.exception.ResourceNotFoundException;
import com.bitcoderdotcom.librarymanagementsystem.exception.UnauthorizedException;
import com.bitcoderdotcom.librarymanagementsystem.repository.BookRepository;
import com.bitcoderdotcom.librarymanagementsystem.repository.BorrowRepository;
import com.bitcoderdotcom.librarymanagementsystem.repository.UserRepository;
import com.bitcoderdotcom.librarymanagementsystem.service.BookService;
import com.bitcoderdotcom.librarymanagementsystem.service.impl.BookServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BookServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BorrowRepository borrowRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private Principal principalLibrarian, principalMember;
    private Librarian librarian;
    private Member member;
    private Book book1;
    private Book book2;
    private List<Book> books;

    @Before
    public void setUp() {

        librarian = new Librarian();
        librarian.setEmail("thomas@email.com");
        librarian.setName("romeo");
        librarian.setRoles(Roles.LIBRARIAN);

        member = new Member();
        member.setEmail("agatha@email.com");
        member.setName("agatha");
        member.setRoles(Roles.MEMBER);

        //Mock the Principal
        principalLibrarian = Mockito.mock(Principal.class);
        when(principalLibrarian.getName()).thenReturn(librarian.getEmail());

        principalMember = Mockito.mock(Principal.class);
        lenient().when(principalMember.getName()).thenReturn(member.getEmail());

        book1 = new Book();
        book1.setTitle("Test Book 1");
        book1.setAuthor("Test Author 1");
        book1.setIsbn("1234567890");
        book1.setGenre(Genre.FICTION);
        book1.setQuantity(1);

        book2 = new Book();
        book2.setTitle("Test Book 2");
        book2.setAuthor("Test Author 2");
        book2.setIsbn("0987654321");
        book2.setGenre(Genre.SOFTWARE_PROGRAMMING);
        book2.setQuantity(2);

        books = new ArrayList<>();
        books.add(book1);
        books.add(book2);

        Page<Book> bookPage = new PageImpl<>(books);
        when(bookRepository.findAll(any(Pageable.class))).thenReturn(bookPage);
        when(userRepository.findById(librarian.getId())).thenReturn(Optional.of(librarian));
        when(userRepository.findByEmail(librarian.getEmail())).thenReturn(Optional.of(librarian));
        lenient().when(userRepository.findByEmail(member.getEmail())).thenReturn(Optional.of(member));
        when(bookRepository.findById("book1")).thenReturn(Optional.of(book1));
    }

    @Test
    @DisplayName("Test Insert Book Into Shelve")
    public void testInsertBookIntoShelve() {
        // Arrange
        Principal principal = Mockito.mock(Principal.class);
        when(principal.getName()).thenReturn(librarian.getEmail());

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(librarian));

        BookDto bookDto = new BookDto(book1.getTitle(), book1.getAuthor(), book1.getIsbn(), book1.getGenre(), book1.getQuantity());

        when(bookRepository.save(any(Book.class))).thenReturn(book1);

        // Act
        ResponseEntity<ApiResponse<BookDto.Response>> response = bookService.insertBookIntoShelve(bookDto, principal);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Book inserted successfully to " + book1.getGenre() + " shelve by " + librarian.getName(), response.getBody().getMessage());
    }

    @Test
    @DisplayName("Test Get Book By Id")
    public void testGetBookById() {
        // Arrange
        String id = "book1";
        Book book = new Book();
        book.setId(id);

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        // Act
        ResponseEntity<ApiResponse<BookDto.Response>> response = bookService.getBookById(id);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Book fetched successfully", response.getBody().getMessage());
        assertEquals(id, response.getBody().getData().getId());
    }

    @Test
    @DisplayName("Test Get All Books")
    public void testGetAllBooks() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 5, Sort.by("title"));

        // Act
        ResponseEntity<ApiResponse<Page<BookDto.Response>>> response = bookService.getAllBooks(principalLibrarian, pageable);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("All books fetched successfully", response.getBody().getMessage());
        assertEquals(books.size(), response.getBody().getData().getTotalElements());
    }

    @Test
    @DisplayName("Test Get Book By Id - Book Not Found")
    public void testGetBookByIdNotFound() {
        // Arrange
        String id = "book1";

        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(ResourceNotFoundException.class, () -> bookService.getBookById(id));
    }

    @Test
    @DisplayName("Test Update Book Details - User Not Authorized")
    public void testUpdateBookDetailsNotAuthorized() {
        // Arrange
        Principal principal = Mockito.mock(Principal.class);
        when(principal.getName()).thenReturn("member@example.com");

        Member member = new Member();
        member.setEmail("member@example.com");
        member.setName("john");
        member.setRoles(Roles.MEMBER);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(member));

        String bookId = "book1";
        BookDto bookDto = new BookDto();

        // Act and Assert
        assertThrows(UnauthorizedException.class, () -> bookService.updateBookDetails(bookId, bookDto, principal));
    }

    @Test
    @DisplayName("Test Get Books By Librarian Id - User Not Found")
    public void testGetBooksByLibrarianIdNotFound() {
        // Arrange
        Principal principal = Mockito.mock(Principal.class);
        lenient().when(principal.getName()).thenReturn(librarian.getEmail());
        String librarianId = "librarian1";

        when(userRepository.findById(librarianId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(ResourceNotFoundException.class, () -> bookService.getBooksByLibrarianId(librarianId, principal));
    }

    @Test
    @DisplayName("Test Update Book Details")
    public void testUpdateBookDetails() {
        // Arrange
        String bookId = "book1";
        BookDto bookDto = new BookDto();
        bookDto.setTitle("Updated Book");
        bookDto.setAuthor("Updated Author");
        bookDto.setIsbn("0987654321");
        bookDto.setGenre(Genre.SCI_FI);
        bookDto.setQuantity(2);

        when(bookRepository.save(any(Book.class))).thenReturn(book1);

        // Act
        ResponseEntity<ApiResponse<BookDto.Response>> response = bookService.updateBookDetails(bookId, bookDto, principalLibrarian);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Book details updated successfully", response.getBody().getMessage());
        assertEquals(bookDto.getTitle(), response.getBody().getData().getTitle());
    }

    @Test
    @DisplayName("Test Get Books By Librarian Id")
    public void testGetBooksByLibrarianId() {
        // Arrange
        when(bookRepository.findByUser(any(User.class))).thenReturn(books);

        // Act
        ResponseEntity<ApiResponse<List<BookDto.Response>>> response = bookService.getBooksByLibrarianId(librarian.getId(), principalLibrarian);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Books inserted by librarian with id " + librarian.getId() + " fetched successfully", response.getBody().getMessage());
        assertEquals(books.size(), response.getBody().getData().size());
    }


    @Test
    @DisplayName("Test Search Books - By Title")
    public void testSearchBooksByTitle() {
        // Arrange
        String title = "Test Book";
        when(bookRepository.findAll(any(Specification.class))).thenReturn(books);

        // Act
        ResponseEntity<ApiResponse<List<BookDto.Response>>> response = bookService.searchBooks(title, null, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Books searched successfully", response.getBody().getMessage());
        assertEquals(books.size(), response.getBody().getData().size());
    }

    @Test
    @DisplayName("Test Search Books - By Author")
    public void testSearchBooksByAuthor() {
        // Arrange
        String author = "Test Author";
        when(bookRepository.findAll(any(Specification.class))).thenReturn(books);

        // Act
        ResponseEntity<ApiResponse<List<BookDto.Response>>> response = bookService.searchBooks(null, author, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Books searched successfully", response.getBody().getMessage());
        assertEquals(books.size(), response.getBody().getData().size());
    }

    @Test
    @DisplayName("Test Search Books - By Genre")
    public void testSearchBooksByGenre() {
        // Arrange
        Genre genre = Genre.FICTION;
        when(bookRepository.findAll(any(Specification.class))).thenReturn(books);

        // Act
        ResponseEntity<ApiResponse<List<BookDto.Response>>> response = bookService.searchBooks(null, null, genre);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Books searched successfully", response.getBody().getMessage());
        assertEquals(books.size(), response.getBody().getData().size());
    }

    @Test
    @DisplayName("Test Remove Book From Shelve - User Not Found")
    public void testRemoveBookFromShelveUserNotFound() {
        // Arrange
        String bookId = "book1";
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(ResourceNotFoundException.class, () -> bookService.removeBookFromShelve(bookId, principalMember));
    }

    @Test
    @DisplayName("Test Remove Book From Shelve - User Not Authorized")
    public void testRemoveBookFromShelveUserNotAuthorized() {
        // Arrange
        String bookId = "book1";
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(member));

        // Act and Assert
        assertThrows(UnauthorizedException.class, () -> bookService.removeBookFromShelve(bookId, principalMember));
    }

    @Test
    @DisplayName("Test Remove Book From Shelve - Book Not Returned")
    public void testRemoveBookFromShelveBookNotReturned() {
        // Arrange
        String bookId = "book1";
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(librarian));
        when(bookRepository.findById(anyString())).thenReturn(Optional.of(book1));

        Borrow borrow = new Borrow();
        borrow.setReturnedAt(null);
        List<Borrow> borrows = new ArrayList<>();
        borrows.add(borrow);
        when(borrowRepository.findByBookId(anyString())).thenReturn(borrows);

        // Act and Assert
        assertThrows(BadRequestException.class, () -> bookService.removeBookFromShelve(bookId, principalLibrarian));
    }

    @Test
    @DisplayName("Test Remove Book From Shelve - Success")
    public void testRemoveBookFromShelveSuccess() {
        // Arrange
        String bookId = "book1";
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(librarian));
        when(bookRepository.findById(anyString())).thenReturn(Optional.of(book1));

        Borrow borrow = new Borrow();
        borrow.setReturnedAt(LocalDateTime.now());
        List<Borrow> borrows = new ArrayList<>();
        borrows.add(borrow);
        when(borrowRepository.findByBookId(anyString())).thenReturn(borrows);

        // Act
        ResponseEntity<ApiResponse<String>> response = bookService.removeBookFromShelve(bookId, principalLibrarian);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Book removed successfully", response.getBody().getMessage());
    }
}