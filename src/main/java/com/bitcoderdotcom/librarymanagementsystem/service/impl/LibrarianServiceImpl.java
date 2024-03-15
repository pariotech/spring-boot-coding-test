package com.bitcoderdotcom.librarymanagementsystem.service.impl;

import com.bitcoderdotcom.librarymanagementsystem.constant.Roles;
import com.bitcoderdotcom.librarymanagementsystem.dto.ApiResponse;
import com.bitcoderdotcom.librarymanagementsystem.dto.LibrarianDto;
import com.bitcoderdotcom.librarymanagementsystem.entities.Book;
import com.bitcoderdotcom.librarymanagementsystem.entities.Librarian;
import com.bitcoderdotcom.librarymanagementsystem.entities.User;
import com.bitcoderdotcom.librarymanagementsystem.exception.ResourceNotFoundException;
import com.bitcoderdotcom.librarymanagementsystem.exception.UnauthorizedException;
import com.bitcoderdotcom.librarymanagementsystem.repository.LibrarianRepository;
import com.bitcoderdotcom.librarymanagementsystem.repository.UserRepository;
import com.bitcoderdotcom.librarymanagementsystem.service.LibrarianService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class LibrarianServiceImpl implements LibrarianService {

    private final LibrarianRepository librarianRepository;
    private final UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<ApiResponse<LibrarianDto.Response>> getLibrarianById(String id) {
        log.info("Fetching librarian with id: {}", id);
        Librarian librarian = librarianRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Librarian", "id", id));
        LibrarianDto.Response librarianDto = convertEntityToDto(librarian);
        ApiResponse<LibrarianDto.Response> apiResponse = new ApiResponse<>(
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                true,
                "Librarian fetched successfully",
                librarianDto
        );
        log.info("Librarian fetched successfully with id: {}", id);
        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public ResponseEntity<ApiResponse<LibrarianDto.Response>> updateLibrarianDetails(LibrarianDto librarianDto, Principal principal) {
        log.info("Updating librarian details for: {}", principal.getName());
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", principal.getName()));
        if (user.getRoles() != Roles.LIBRARIAN) {
            throw new UnauthorizedException("Only a Librarian can update their details");
        }
        user.setEmail(librarianDto.getEmail());
        user.setPassword(passwordEncoder.encode(librarianDto.getPassword()));
        userRepository.save(user);
        LibrarianDto.Response librarianResponse = convertEntityToDto((Librarian) user);
        ApiResponse<LibrarianDto.Response> apiResponse = new ApiResponse<>(
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                true,
                "Librarian details updated successfully",
                librarianResponse
        );
        log.info("Librarian details updated successfully for: {}", principal.getName());
        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public ResponseEntity<ApiResponse<List<LibrarianDto.Response>>> getAllLibrarians() {
        log.info("Fetching all librarians");
        List<Librarian> librarians = librarianRepository.findAll();
        if (librarians.isEmpty()) {
            throw new ResourceNotFoundException("Librarian", "All", null);
        }
        List<LibrarianDto.Response> librarianDtos = librarians.stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList());
        ApiResponse<List<LibrarianDto.Response>> apiResponse = new ApiResponse<>(
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                true,
                "All librarians fetched successfully",
                librarianDtos
        );
        log.info("All librarians fetched successfully");
        return ResponseEntity.ok(apiResponse);
    }


    @Override
    public ResponseEntity<ApiResponse<String>> removeLibrarian(String id, String email) {
        log.info("Removing librarian with id: {}", id);
        Librarian librarian = librarianRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Librarian", "id", id));
        if (!librarian.getEmail().equals(email)) {
            throw new UnauthorizedException("You are not authorized to perform this operation");
        }
        librarianRepository.delete(librarian);
        ApiResponse<String> apiResponse = new ApiResponse<>(
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                true,
                "Librarian removed successfully",
                "Librarian with id " + id + " removed."
        );
        log.info("Librarian removed successfully with id: {}", id);
        return ResponseEntity.ok(apiResponse);
    }

    private LibrarianDto.Response convertEntityToDto(Librarian librarian) {
        LibrarianDto.Response librarianDto = new LibrarianDto.Response();
        librarianDto.setId(librarian.getId());
        librarianDto.setName(librarian.getName());
        librarianDto.setEmail(librarian.getEmail());
        librarianDto.setRoles(librarian.getRoles());
        librarianDto.setBookIds(librarian.getBooks().stream()
                .map(Book::getId)
                .collect(Collectors.toList()));
        return librarianDto;
    }
}
