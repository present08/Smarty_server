package com.green.smarty.controller;

import com.green.smarty.dto.CommunityDTO;
import com.green.smarty.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/notice")
public class CommunityController {
    private final CommunityService communityService;

    @GetMapping("/allList")
    public ResponseEntity<List<CommunityDTO>> getCommunityPosts() {
        List<CommunityDTO> posts = communityService.getCommunityPosts();
        return ResponseEntity.ok(posts);
    }
}
