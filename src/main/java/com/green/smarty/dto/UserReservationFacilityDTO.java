package com.green.smarty.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class UserReservationFacilityDTO {
    private String facility_id;
    private String facility_name;
    private String reservation_id;
    private String court_id;

}
