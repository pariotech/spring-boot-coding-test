package com.bitcoderdotcom.librarymanagementsystem.entities;

import com.bitcoderdotcom.librarymanagementsystem.constant.Roles;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "librarians")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Librarian extends User{

    @OneToMany(mappedBy = "user")
    private List<Book> books;
}
