package com.green.smarty.dto;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Setter
@Builder
public class ReservationUserDTO {
    private String user_id;

    private String user_name;

    private LocalDateTime reservation_start;

    private LocalDateTime reservation_end;

    private String court_name;

    private String facility_name;

    private String reservation_id;
}
