package com.bitcoderdotcom.librarymanagementsystem.repository;

import com.bitcoderdotcom.librarymanagementsystem.entities.Borrow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BorrowRepository extends JpaRepository<Borrow, String> {
    List<Borrow> findByBookId(String id);
}
