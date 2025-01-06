package com.green.smarty.mapper;

import com.green.smarty.dto.CommunityDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommunityMapper {
    List<CommunityDTO> getCommunityPosts();
}