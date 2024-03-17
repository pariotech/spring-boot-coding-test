package com.bitcoderdotcom.librarymanagementsystem.security.services;

import com.bitcoderdotcom.librarymanagementsystem.entities.Librarian;
import com.bitcoderdotcom.librarymanagementsystem.entities.Member;
import com.bitcoderdotcom.librarymanagementsystem.entities.User;
import com.bitcoderdotcom.librarymanagementsystem.repository.LibrarianRepository;
import com.bitcoderdotcom.librarymanagementsystem.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final LibrarianRepository librarianRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Trying to get user by email");
        Librarian customer = librarianRepository.findByEmail(email)
                .orElse(null);

        if (customer != null) {
            log.info("Found LIBRARIAN user: {}", email);
            return UserDetailsImpl.build(customer);
        } else {
            log.info("LIBRARIAN not found. Checking for MEMBER: {}", email);
        }

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Member not found with email: {}", email);
                    return new UsernameNotFoundException("Member not found with email: " + email);
                });

        log.info("Found MEMBER user: {}", email);
        return buildUserDetails(member);
    }

    private UserDetails buildUserDetails(User user) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        if (user instanceof Librarian) {
            authorities.add(new SimpleGrantedAuthority("LIBRARIAN"));
        } else if (user instanceof Member) {
            authorities.add(new SimpleGrantedAuthority("MEMBER"));
        }
        log.info("Building UserDetails for user: {}", user.getName());
        return new UserDetailsImpl(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }
}
