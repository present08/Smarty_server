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
public class ClassResponseDTO {
    List<UserClassDTO> classDTO;
    Map<String, List<String>> weekday;
}
