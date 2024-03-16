package com.bitcoderdotcom.librarymanagementsystem.controller;

import com.bitcoderdotcom.librarymanagementsystem.dto.*;
import com.bitcoderdotcom.librarymanagementsystem.service.BorrowService;
import com.bitcoderdotcom.librarymanagementsystem.service.MemberService;
import io.undertow.util.BadRequestException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/lms/v1/member")
@AllArgsConstructor
public class MemberController {

    private final MemberService memberService;
//    private BorrowService borrowService;
//
//    @PostMapping("/borrow")
//    public ResponseEntity<ApiResponse<BorrowDto.Response>> borrowBook(@RequestBody BorrowRequestDto borrowRequestDto, Principal principal) {
//        return borrowService.borrowBook(borrowRequestDto, principal);
//    }
//
//    @PostMapping("/return")
//    public ResponseEntity<ApiResponse<BorrowDto.Response>> returnBook(@RequestBody ReturnRequestDto returnRequestDto, Principal principal) {
//        return borrowService.returnBook(returnRequestDto, principal);
//    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MemberDto.Response>> getMemberById(@PathVariable String id, Principal principal) {
        return memberService.getMemberById(id, principal);
    }

    @PatchMapping("/updateDetails")
    public ResponseEntity<ApiResponse<MemberDto.Response>> updateMemberDetails(@RequestBody MemberDto memberDto, Principal principal) {
        return memberService.updateMemberDetails(memberDto, principal);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> removeMember(@PathVariable String id, @RequestHeader("email") String email) throws BadRequestException {
        return memberService.removeMember(id, email);
    }
}
