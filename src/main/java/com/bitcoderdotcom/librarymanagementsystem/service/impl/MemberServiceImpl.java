package com.bitcoderdotcom.librarymanagementsystem.service.impl;

import com.bitcoderdotcom.librarymanagementsystem.constant.Roles;
import com.bitcoderdotcom.librarymanagementsystem.dto.ApiResponse;
import com.bitcoderdotcom.librarymanagementsystem.dto.MemberDto;
import com.bitcoderdotcom.librarymanagementsystem.entities.Book;
import com.bitcoderdotcom.librarymanagementsystem.entities.Member;
import com.bitcoderdotcom.librarymanagementsystem.entities.User;
import com.bitcoderdotcom.librarymanagementsystem.exception.ResourceNotFoundException;
import com.bitcoderdotcom.librarymanagementsystem.exception.UnauthorizedException;
import com.bitcoderdotcom.librarymanagementsystem.repository.MemberRepository;
import com.bitcoderdotcom.librarymanagementsystem.repository.UserRepository;
import com.bitcoderdotcom.librarymanagementsystem.service.MemberService;
import io.undertow.util.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<ApiResponse<MemberDto.Response>> getMemberById(String id, Principal principal) {
        log.info("Fetching Member with id: {}", id);
        Member principalMember = memberRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", principal.getName()));
        if (!principalMember.getId().equals(id)) {
            throw new UnauthorizedException("You can only access your own information");
        }
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member", "id", id));
        MemberDto.Response memberDto = convertEntityToDto(member);

        List<String> borrowedBookIds = member.getBorrows().stream()
                .map(borrow -> borrow.getBook().getId())
                .collect(Collectors.toList());

        memberDto.setBookIds(borrowedBookIds);
        ApiResponse<MemberDto.Response> apiResponse = new ApiResponse<>(
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                true,
                "Member fetched successfully",
                memberDto
        );
        log.info("Member fetched successfully with id: {}", id);
        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public ResponseEntity<ApiResponse<MemberDto.Response>> updateMemberDetails(MemberDto memberDto, Principal principal) {
        log.info("Updating member details for: {}", principal.getName());
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", principal.getName()));
        if (user.getRoles() != Roles.MEMBER) {
            throw new UnauthorizedException("Only a member can update their details");
        }
        if (memberDto.getEmail() != null && !memberDto.getEmail().isEmpty()) {
            user.setEmail(memberDto.getEmail());
        }
        if (memberDto.getPassword() != null && !memberDto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(memberDto.getPassword()));
        }
        userRepository.save(user);
        MemberDto.Response memberResponse = convertEntityToDto((Member) user);
        ApiResponse<MemberDto.Response> apiResponse = new ApiResponse<>(
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                true,
                "Member details updated successfully",
                memberResponse
        );
        log.info("Member details updated successfully for: {}", principal.getName());
        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public ResponseEntity<ApiResponse<Page<MemberDto.Response>>> getAllMembers(Principal principal, Pageable pageable) {
        log.info("Fetching all members");
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", principal.getName()));
        if (user.getRoles() != Roles.LIBRARIAN) {
            throw new UnauthorizedException("Only a Librarian can fetch all members");
        }
        Page<Member> members = memberRepository.findAll(pageable);
        if (members.isEmpty()) {
            throw new ResourceNotFoundException("Member", "All", null);
        }
        Page<MemberDto.Response> memberDtos = members.map(member -> {
            MemberDto.Response memberDto = convertEntityToDto(member);

            List<String> borrowedBookIds = member.getBorrows().stream()
                    .map(borrow -> borrow.getBook().getId())
                    .collect(Collectors.toList());

            memberDto.setBookIds(borrowedBookIds);

            return memberDto;
        });
        ApiResponse<Page<MemberDto.Response>> apiResponse = new ApiResponse<>(
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                true,
                "All members fetched successfully",
                memberDtos
        );
        log.info("All members fetched successfully");
        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public ResponseEntity<ApiResponse<String>> removeMember(String id, String email) throws BadRequestException {
        log.info("Removing member with id: {}", id);
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member", "id", id));
        if (!member.getEmail().equals(email)) {
            throw new UnauthorizedException("You are not authorized to perform this operation");
        }
        if (member.isWithBorrowedBook()) {
            throw new BadRequestException("Member with id " + id + " has borrowed books. They must return them before their account can be removed.");
        }
        memberRepository.delete(member);
        ApiResponse<String> apiResponse = new ApiResponse<>(
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                true,
                "Member account removed successfully",
                "Member with id " + id + " removed."
        );
        log.info("Member removed successfully with id: {}", id);
        return ResponseEntity.ok(apiResponse);
    }

    private MemberDto.Response convertEntityToDto(Member member) {
        MemberDto.Response memberDto = new MemberDto.Response();
        memberDto.setId(member.getId());
        memberDto.setName(member.getName());
        memberDto.setEmail(member.getEmail());
        memberDto.setRoles(member.getRoles());
        memberDto.setWithBorrowedBook(member.isWithBorrowedBook());
        memberDto.setBookIds(member.getBooks().stream()
                .map(Book::getId)
                .collect(Collectors.toList()));
        return memberDto;
    }
}
