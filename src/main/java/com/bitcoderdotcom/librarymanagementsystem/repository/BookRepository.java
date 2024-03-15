package com.bitcoderdotcom.librarymanagementsystem.repository;

import com.bitcoderdotcom.librarymanagementsystem.entities.Book;
import com.bitcoderdotcom.librarymanagementsystem.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, String>, JpaSpecificationExecutor<Book> {


    List<Book> findByUser(User user);
}
