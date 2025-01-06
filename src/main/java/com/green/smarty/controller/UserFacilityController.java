package com.green.smarty.controller;

import com.green.smarty.dto.FacilityDTO;
import com.green.smarty.dto.ProductDTO;
import com.green.smarty.dto.UserReservationFacilityDTO;
import com.green.smarty.service.UserFacilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/facilities")

public class UserFacilityController {
    @Autowired
    private UserFacilityService userFacilityService;

    @GetMapping("/list")
    public List<FacilityDTO> getFacilities() {
        return userFacilityService.getAllFacilities();
    }

    @GetMapping("/{facility_id}/products")
    public List<ProductDTO> getProductsByFacility(@PathVariable String facility_id) {
        return userFacilityService.getProductsByFacilityId(facility_id);
    }

    @GetMapping("/reservation/{user_id}")
    public List<UserReservationFacilityDTO> getUserReservationFacility(@PathVariable String user_id) {
        return userFacilityService.getUserReservationFacility(user_id);
    }
}
