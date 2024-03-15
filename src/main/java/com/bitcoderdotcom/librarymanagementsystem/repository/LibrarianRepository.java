package com.bitcoderdotcom.librarymanagementsystem.repository;


import com.bitcoderdotcom.librarymanagementsystem.entities.Librarian;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LibrarianRepository extends JpaRepository<Librarian, String> {

    Optional<Librarian> findByEmail(String email);
}
