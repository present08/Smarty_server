package com.green.smarty.service;

import com.green.smarty.dto.CommunityDTO;
import com.green.smarty.mapper.CommunityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor

public class CommunityService {

    private final CommunityMapper communityMapper;

    public List<CommunityDTO> getCommunityPosts() {
        return communityMapper.getCommunityPosts();
    }

}
