package com.green.smarty.dto;

import com.green.smarty.vo.EnrollmentVO;
import com.green.smarty.vo.ReservationVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class UserActivityDTO {
    List<ReservationVO> reservationList;
    List<EnrollmentClassDTO> enrollmentList;
}



