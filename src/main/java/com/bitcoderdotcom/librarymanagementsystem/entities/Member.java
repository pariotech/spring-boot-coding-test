package com.bitcoderdotcom.librarymanagementsystem.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "members")
@AllArgsConstructor
public class Member extends User{

}
