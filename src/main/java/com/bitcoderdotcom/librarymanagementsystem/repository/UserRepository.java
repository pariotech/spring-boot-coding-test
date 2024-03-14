package com.bitcoderdotcom.librarymanagementsystem.repository;

import com.bitcoderdotcom.librarymanagementsystem.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByName(String username);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String name);
}
