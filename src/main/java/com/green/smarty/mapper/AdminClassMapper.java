package com.green.smarty.mapper;

import com.green.smarty.dto.ClassAdminDTO;
import com.green.smarty.vo.ClassDetailVO;
import com.green.smarty.vo.ClassVO;
import com.green.smarty.vo.CourtVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AdminClassMapper {
    void register(ClassAdminDTO classAdminDTO);
    void registerDetail(ClassDetailVO classDetailVO);
    List<ClassVO> getList(String facility_id);
    ClassVO read(String class_id);
    List<ClassDetailVO> getDetailList(String class_id);
    void modify(ClassVO classVO);
    void remove(String class_id);
    void removeDetail(String class_id);
}
