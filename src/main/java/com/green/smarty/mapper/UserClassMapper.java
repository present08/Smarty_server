package com.green.smarty.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.green.smarty.dto.UserClassDTO;
import com.green.smarty.vo.ClassDetailVO;
import com.green.smarty.vo.ClassVO;
import com.green.smarty.vo.EnrollmentVO;

@Mapper
public interface UserClassMapper {
    void insertClass(ClassVO vo);

    void insertClassDetail(ClassDetailVO vo);

    List<ClassVO> getClassAll();

    ClassVO getClassVO(String class_id);

    List<UserClassDTO> getClassDTO();

    List<ClassDetailVO> getClassDetail();

    List<EnrollmentVO> getEnrollment();
    
    List<EnrollmentVO> getEnrollSize(String class_id);

    EnrollmentVO enrollCheck(Map<String,String> enrollData);

    void classEnroll(EnrollmentVO vo);
}
