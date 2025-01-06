package com.green.smarty.mapper;

import com.green.smarty.vo.FacilityAttachVO;
import com.green.smarty.vo.FacilityVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface AdminFacilityMapper {

    void register(FacilityVO facilityVO);

    void fileUpload(Map filesUpload);

    List<FacilityVO> getList();

    FacilityVO read(String facility_id);

    List<FacilityAttachVO> getImages(String facility_id);

    void modify(FacilityVO facilityVO);

    void remove(String id);

    void fileRemove(String facility_id);
}
