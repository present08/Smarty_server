package com.green.smarty.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Setter
@Builder
public class AdminStatusDTO {
    @Builder.Default
    private List<AdminReservationDTO> reservationDTOList = new ArrayList<>();
    @Builder.Default
    private List<AdminEnrollmentDTO> enrollmentDTOList = new ArrayList<>();
}
