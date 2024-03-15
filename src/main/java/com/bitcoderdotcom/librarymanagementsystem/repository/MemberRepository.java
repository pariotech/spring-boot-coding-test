package com.bitcoderdotcom.librarymanagementsystem.repository;

import com.bitcoderdotcom.librarymanagementsystem.entities.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {
    Optional<Member> findByEmail(String email);
}
