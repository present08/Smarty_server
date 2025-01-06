package com.green.smarty.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserReservationDTO {
    List<Map<String, Integer>> btnData;
    int iterable;
    String reservation_id;
}
