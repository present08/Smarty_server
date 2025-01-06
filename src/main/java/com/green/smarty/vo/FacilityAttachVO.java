package com.green.smarty.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FacilityAttachVO {
    private String facility_id;
    private String origin_path;
    private String thumbnail_path;
    private String file_name;
}
