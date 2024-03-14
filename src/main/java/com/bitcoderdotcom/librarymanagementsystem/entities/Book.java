package com.bitcoderdotcom.librarymanagementsystem.entities;


import com.bitcoderdotcom.librarymanagementsystem.constant.Genre;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String author;
    private String ISBN;
    @Enumerated(EnumType.STRING)
    private Genre genre;
    private long quantity;
}
