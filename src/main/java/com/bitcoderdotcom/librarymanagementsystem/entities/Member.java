package com.bitcoderdotcom.librarymanagementsystem.entities;

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
@Table(name = "members")
@AllArgsConstructor
@Getter
@Setter
public class Member extends User{

    @OneToMany(mappedBy = "user")
    private List<Book> books;

    public Member() {
        super();
        this.setId(generateCustomUUID());
    }

    private String generateCustomUUID() {
        return "Mem"+ UUID.randomUUID().toString().substring(0, 5);
    }
}
