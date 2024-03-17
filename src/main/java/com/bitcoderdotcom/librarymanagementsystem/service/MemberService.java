package com.bitcoderdotcom.librarymanagementsystem.service;

import com.bitcoderdotcom.librarymanagementsystem.dto.ApiResponse;
import com.bitcoderdotcom.librarymanagementsystem.dto.MemberDto;
import io.undertow.util.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;

public interface MemberService {

    ResponseEntity<ApiResponse<MemberDto.Response>> getMemberById(String id, Principal principal);
    ResponseEntity<ApiResponse<Page<MemberDto.Response>>> getAllMembers(Principal principal, Pageable pageable);
    ResponseEntity<ApiResponse<MemberDto.Response>> updateMemberDetails(MemberDto memberDto, Principal principal);
    ResponseEntity<ApiResponse<String>> removeMember(String id, String email) throws BadRequestException;
}
