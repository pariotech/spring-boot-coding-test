package com.bitcoderdotcom.librarymanagementsystem.repository;

import com.bitcoderdotcom.librarymanagementsystem.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByName(String username);

    boolean existsByEmail(String email);
}
