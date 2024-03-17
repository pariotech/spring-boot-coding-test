package com.bitcoderdotcom.librarymanagementsystem;

import com.bitcoderdotcom.librarymanagementsystem.constant.Genre;
import com.bitcoderdotcom.librarymanagementsystem.constant.Roles;
import com.bitcoderdotcom.librarymanagementsystem.dto.ApiResponse;
import com.bitcoderdotcom.librarymanagementsystem.dto.BorrowDto;
import com.bitcoderdotcom.librarymanagementsystem.dto.BorrowRequestDto;
import com.bitcoderdotcom.librarymanagementsystem.dto.ReturnRequestDto;
import com.bitcoderdotcom.librarymanagementsystem.entities.Book;
import com.bitcoderdotcom.librarymanagementsystem.entities.Borrow;
import com.bitcoderdotcom.librarymanagementsystem.entities.Librarian;
import com.bitcoderdotcom.librarymanagementsystem.entities.Member;
import com.bitcoderdotcom.librarymanagementsystem.exception.BadRequestException;
import com.bitcoderdotcom.librarymanagementsystem.exception.ResourceNotFoundException;
import com.bitcoderdotcom.librarymanagementsystem.exception.UnauthorizedException;
import com.bitcoderdotcom.librarymanagementsystem.repository.BookRepository;
import com.bitcoderdotcom.librarymanagementsystem.repository.BorrowRepository;
import com.bitcoderdotcom.librarymanagementsystem.repository.UserRepository;
import com.bitcoderdotcom.librarymanagementsystem.service.impl.BookServiceImpl;
import com.bitcoderdotcom.librarymanagementsystem.service.impl.BorrowServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BorrowServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BorrowRepository borrowRepository;

    @InjectMocks
    private BorrowServiceImpl borrowService;

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

        when(bookRepository.findById("book1")).thenReturn(Optional.of(book1));
    }

    @Test
    @DisplayName("Test Borrow Book - User Not Found")
    public void testBorrowBookUserNotFound() {
        // Arrange
        BorrowRequestDto borrowRequestDto = new BorrowRequestDto();
        borrowRequestDto.setBookId("book1");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(ResourceNotFoundException.class, () -> borrowService.borrowBook(borrowRequestDto, principalMember));
    }

    @Test
    @DisplayName("Test Borrow Book - User Not Authorized")
    public void testBorrowBookUserNotAuthorized() {
        // Arrange
        BorrowRequestDto borrowRequestDto = new BorrowRequestDto();
        borrowRequestDto.setBookId("book1");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(member));
        member.setRoles(Roles.LIBRARIAN);


        // Act and Assert
        assertThrows(UnauthorizedException.class, () -> borrowService.borrowBook(borrowRequestDto, principalMember));
    }

    @Test
    @DisplayName("Test Borrow Book - Book Not Returned")
    public void testBorrowBookBookNotReturned() {
        // Arrange
        BorrowRequestDto borrowRequestDto = new BorrowRequestDto();
        borrowRequestDto.setBookId("book1");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(member));
        member.setWithBorrowedBook(true);

        // Act and Assert
        assertThrows(BadRequestException.class, () -> borrowService.borrowBook(borrowRequestDto, principalMember));
    }

    @Test
    @DisplayName("Test Borrow Book - Book Out of Stock")
    public void testBorrowBookOutOfStock() {
        // Arrange
        BorrowRequestDto borrowRequestDto = new BorrowRequestDto();
        borrowRequestDto.setBookId("book1");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(member));
        member.setWithBorrowedBook(false);
        book1.setQuantity(0);

        // Act and Assert
        assertThrows(BadRequestException.class, () -> borrowService.borrowBook(borrowRequestDto, principalMember));
    }

    @Test
    @DisplayName("Test Borrow Book - Success")
    public void testBorrowBookSuccess() {
        // Arrange
        BorrowRequestDto borrowRequestDto = new BorrowRequestDto();
        borrowRequestDto.setBookId("book1");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(member));
        member.setWithBorrowedBook(false);
        book1.setQuantity(1);

        // Act
        ResponseEntity<ApiResponse<BorrowDto.Response>> response = borrowService.borrowBook(borrowRequestDto, principalMember);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Book borrowed successfully by " + member.getName(), response.getBody().getMessage());
    }

    @Test
    @DisplayName("Test Return Book - User Not Found")
    public void testReturnBookUserNotFound() {
        // Arrange
        ReturnRequestDto returnRequestDto = new ReturnRequestDto();
        returnRequestDto.setBorrowId("borrow1");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(ResourceNotFoundException.class, () -> borrowService.returnBook(returnRequestDto, principalMember));
    }

    @Test
    @DisplayName("Test Return Book - User Not Authorized")
    public void testReturnBookUserNotAuthorized() {
        // Arrange
        ReturnRequestDto returnRequestDto = new ReturnRequestDto();
        returnRequestDto.setBorrowId("borrow1");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(member));
        member.setRoles(Roles.LIBRARIAN);

        // Act and Assert
        assertThrows(UnauthorizedException.class, () -> borrowService.returnBook(returnRequestDto, principalLibrarian));
    }

    @Test
    @DisplayName("Test Return Book - Borrow Not Found")
    public void testReturnBookBorrowNotFound() {
        // Arrange
        ReturnRequestDto returnRequestDto = new ReturnRequestDto();
        returnRequestDto.setBorrowId("borrow1");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(member));
        when(borrowRepository.findById(anyString())).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(ResourceNotFoundException.class, () -> borrowService.returnBook(returnRequestDto, principalMember));
    }

    @Test
    @DisplayName("Test Return Book - Book Already Returned")
    public void testReturnBookAlreadyReturned() {
        // Arrange
        ReturnRequestDto returnRequestDto = new ReturnRequestDto();
        returnRequestDto.setBorrowId("borrow1");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(member));
        Borrow borrow = new Borrow();
        borrow.setMember(member);
        borrow.setReturnedAt(LocalDateTime.now());
        when(borrowRepository.findById(anyString())).thenReturn(Optional.of(borrow));

        // Act and Assert
        assertThrows(BadRequestException.class, () -> borrowService.returnBook(returnRequestDto, principalMember));
    }

    @Test
    @DisplayName("Test Return Book - Success")
    public void testReturnBookSuccess() {
        // Arrange
        ReturnRequestDto returnRequestDto = new ReturnRequestDto();
        returnRequestDto.setBorrowId("borrow1");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(member));
        Borrow borrow = new Borrow();
        borrow.setMember(member);
        borrow.setBook(book1);
        when(borrowRepository.findById(anyString())).thenReturn(Optional.of(borrow));

        // Act
        ResponseEntity<ApiResponse<BorrowDto.Response>> response = borrowService.returnBook(returnRequestDto, principalMember);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Book returned successfully by " + member.getName(), response.getBody().getMessage());
    }

    @Test
    @DisplayName("Test Track Borrowed Books - User Not Found")
    public void testTrackBorrowedBooksUserNotFound() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 5);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(ResourceNotFoundException.class, () -> borrowService.trackBorrowedBooks(principalLibrarian, pageable));
    }

    @Test
    @DisplayName("Test Track Borrowed Books - User Not Authorized")
    public void testTrackBorrowedBooksUserNotAuthorized() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 5);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(member));

        // Act and Assert
        assertThrows(UnauthorizedException.class, () -> borrowService.trackBorrowedBooks(principalMember, pageable));
    }

    @Test
    @DisplayName("Test Track Borrowed Books - Success")
    public void testTrackBorrowedBooksSuccess() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 5);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(librarian));
        Page<Borrow> borrows = new PageImpl<>(new ArrayList<>());
        when(borrowRepository.findAll(any(Pageable.class))).thenReturn(borrows);

        // Act
        ResponseEntity<ApiResponse<Page<BorrowDto.Response>>> response = borrowService.trackBorrowedBooks(principalLibrarian, pageable);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Borrowed books tracked successfully", response.getBody().getMessage());
    }
}
