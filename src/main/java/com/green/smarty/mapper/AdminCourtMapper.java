package com.green.smarty.mapper;

import com.green.smarty.vo.CourtVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AdminCourtMapper {
    void register(CourtVO courtVO);
    List<CourtVO> getList(String facility_id);
    CourtVO read(String court_id);
    String maxCourtIdx(String facility_id);
    void modify(CourtVO courtVO);
    void remove(String court_id);
}
