package com.green.smarty.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FacilityDTO {
    private String facility_id;
    private String facility_name; // 시설 종류

    private int quantity; // 수용 가능 인원
    private String open_time;
    private String close_time; // 운영 시간
    private int default_time; // 기본 이용시간

    private int basic_fee;
    private float rate_adjustment; // 기본 및 할증가격
    private int hot_time;

    private String contact;
    private String info;
    private String caution; // 연락처, 주의사항, 이용안내

    private boolean court; // 부가시설 여부
    private boolean product; // 물품등록 여부
    private boolean facility_status; // 이용가능 여부
    private boolean facility_images; // 사진등록 여부

    @Builder.Default
    private List<MultipartFile> files = new ArrayList<>();
    @Builder.Default
    private List<String> file_name = new ArrayList<>(); // 시설 관련 이미지 파일, 파일명

    private String court_id;
    private String court_name;
    private boolean court_status;
}
