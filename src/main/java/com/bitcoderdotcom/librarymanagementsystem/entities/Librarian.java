package com.bitcoderdotcom.librarymanagementsystem.entities;

import com.bitcoderdotcom.librarymanagementsystem.constant.Roles;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "librarians")
@AllArgsConstructor
public class Librarian extends User{


}
