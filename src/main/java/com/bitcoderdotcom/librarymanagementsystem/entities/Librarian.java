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
import java.util.UUID;

@Entity
@Table(name = "librarians")
@AllArgsConstructor
@Getter
@Setter
public class Librarian extends User{

    @OneToMany(mappedBy = "user")
    private List<Book> books;

    public Librarian() {
        super();
        this.setId(generateCustomUUID());
    }

    private String generateCustomUUID() {
        return "Lib"+ UUID.randomUUID().toString().substring(0, 5);
    }
}
