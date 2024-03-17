package com.bitcoderdotcom.librarymanagementsystem.entities;

import com.bitcoderdotcom.librarymanagementsystem.constant.Roles;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@SuperBuilder
@DynamicUpdate
@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "name"),
                @UniqueConstraint(columnNames = "email")
        })
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User implements Serializable {

    @Id
    private String id;
    private String name;
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private Roles roles;


    public User() {
        super();
        this.setId(generateCustomUUID());
    }

    private String generateCustomUUID() {
        return "User"+ UUID.randomUUID().toString().substring(0, 5);
    }
}
