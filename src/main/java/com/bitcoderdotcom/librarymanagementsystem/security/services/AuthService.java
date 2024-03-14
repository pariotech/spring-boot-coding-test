package com.bitcoderdotcom.librarymanagementsystem.security.services;

import com.bitcoderdotcom.librarymanagementsystem.constant.Roles;
import com.bitcoderdotcom.librarymanagementsystem.dto.ApiResponse;
import com.bitcoderdotcom.librarymanagementsystem.dto.SignInRequest;
import com.bitcoderdotcom.librarymanagementsystem.dto.UserRegistrationRequest;
import com.bitcoderdotcom.librarymanagementsystem.entities.Librarian;
import com.bitcoderdotcom.librarymanagementsystem.entities.Member;
import com.bitcoderdotcom.librarymanagementsystem.entities.User;
import com.bitcoderdotcom.librarymanagementsystem.repository.LibrarianRepository;
import com.bitcoderdotcom.librarymanagementsystem.repository.MemberRepository;
import com.bitcoderdotcom.librarymanagementsystem.repository.RoleRepository;
import com.bitcoderdotcom.librarymanagementsystem.repository.UserRepository;
import com.bitcoderdotcom.librarymanagementsystem.security.jwt.JwtUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class AuthService {

    private AuthenticationManager authenticationManager;
    private LibrarianRepository librarianRepository;
    private MemberRepository memberRepository;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private JwtUtils jwtUtils;
    private UserDetailsServiceImpl userDetailsService;


    @Transactional
    public ResponseEntity<ApiResponse<UserRegistrationRequest.Response>> register(UserRegistrationRequest request) {

        log.info("Register method called with request: {}", request);

        String username = request.getName();
        String email = request.getEmail();

        boolean usernameExists = userRepository.existsByName(username);
        boolean emailExists = userRepository.existsByEmail(email);

        if (usernameExists) {
            log.info("Username already taken: {}", username);
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Username is already taken!"
            );
        }

        if (emailExists) {
            log.info("Email already in use: {}", email);
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Email Address already in use!"
            );
        }

        User user;
        if (request.getRole() == Roles.LIBRARIAN) {
            user = new Librarian();
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRoles(request.getRole());
            librarianRepository.save((Librarian) user);
        } else if (request.getRole() == Roles.MEMBER) {
            user = new Member();
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRoles(request.getRole());
            memberRepository.save((Member) user);
        } else {
            throw new IllegalArgumentException("Invalid user type");
        }

        log.info("User registered successfully with username: {}", username);

        UserRegistrationRequest.Response response = new UserRegistrationRequest.Response(
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                true,
                "User registered successfully",
                request.getName()
        );

        return createSuccessResponse("User registered successfully", response);
    }

    public ResponseEntity<ApiResponse<SignInRequest.Response>> signIn(SignInRequest request) {
        log.info("SignIn method called with email: {}", request.getEmail());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            log.info("User signed in successfully with email: {}", request.getEmail());

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            Set<Roles> roles = userDetails.getAuthorities().stream()
                    .map(grantedAuthority -> Roles.valueOf(grantedAuthority.getAuthority()))
                    .collect(Collectors.toSet());

            SignInRequest.Response response = new SignInRequest.Response(
                    userDetails.getId(),
                    jwt,
                    "Bearer",
                    userDetails.getUsername(),
                    roles,
                    jwtUtils.getJwtExpirationDate()
            );

            return createSuccessResponse("User signed in successfully", response);
        } catch (BadCredentialsException e) {
            log.info("Invalid email or password for email: {}", request.getEmail());
            return createBadRequestResponse("Invalid email or password", null);
        }
    }

    public <T> ResponseEntity<ApiResponse<T>> createSuccessResponse(String message, T data) {
        return ResponseEntity.ok(new ApiResponse<>(
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                true,
                message,
                data
        ));
    }

    public <T> ResponseEntity<ApiResponse<T>> createBadRequestResponse(String message, T data) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ApiResponse<>(
                        LocalDateTime.now(),
                        UUID.randomUUID().toString(),
                        false,
                        message,
                        data
                )
        );
    }
}