package com.green.smarty.service;

import com.green.smarty.dto.FacilityDTO;
import com.green.smarty.dto.ProductDTO;
import com.green.smarty.dto.UserReservationFacilityDTO;
import com.green.smarty.mapper.UserFacilityMapper;
import com.green.smarty.mapper.UserProductMapper;
import com.green.smarty.vo.FacilityVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class UserFacilityService {
    @Autowired
    public UserFacilityMapper userFacilityMapper;
    @Autowired
    public UserProductMapper userProductMapper;

    public List<FacilityDTO> getAllFacilities() {
        return userFacilityMapper.getAllFacilities();
    }

    public List<ProductDTO> getProductsByFacilityId(String facility_id) {
        return userProductMapper.getProductsByFacilityId(facility_id);
    }

    public List<UserReservationFacilityDTO> getUserReservationFacility(String user_id) {
        return userFacilityMapper.getUserReservationFacility(user_id);
    }
    // (영준)
    public String getFacilityNameById(String facility_id){
        return userFacilityMapper.getFacilityNameById(facility_id);
    }

}
