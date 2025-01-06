package com.green.smarty.mapper;

import com.green.smarty.dto.FacilityDTO;
import com.green.smarty.dto.UserReservationFacilityDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper

public interface UserFacilityMapper {
    List<FacilityDTO> getAllFacilities();
    List<FacilityDTO> getFacilityById(String facility_id);
    List<UserReservationFacilityDTO> getUserReservationFacility(String user_id);
    //(영준)
    String getFacilityNameById(String facility_id);
    String getClassNameByUserId(String user_id);
}
