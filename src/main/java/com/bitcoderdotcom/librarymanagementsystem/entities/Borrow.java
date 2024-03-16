package com.bitcoderdotcom.librarymanagementsystem.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "borrows")
@Getter
@Setter
public class Borrow {

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    private LocalDateTime borrowedAt;
    private LocalDateTime returnedAt;

    public Borrow(Member member, Book book) {
        this.setId(generateCustomUUID());
        this.member = member;
        this.book = book;
        this.borrowedAt = LocalDateTime.now();
    }

    public Borrow() {
        this.setId(generateCustomUUID());
    }

    private String generateCustomUUID() {
        return "Borrow"+ UUID.randomUUID().toString().substring(0, 5);
    }
}
