package com.green.smarty.vo;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FacilityVO {

    private String facility_id;
    private String facility_name; // 시설 종류

    private String open_time;
    private String close_time; // 운영 시간
    private int default_time; // 기본 이용시간

    private int basic_fee;
    private float rate_adjustment; // 기본 및 할증가격
    private int hot_time;          // 0: 기본가격, 1: 조조할인, 2: 야간할증, 3: 모두

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

    public void changeName(String facility_name) {this.facility_name = facility_name;}
    public void changeOpenTime(String open_time) {this.open_time = open_time;}
    public void changeCloseTime(String close_time) {this.close_time = close_time;}
    public void changeDefaultTIme(int default_time) {this.default_time = default_time;}
    public void changeRate(float rate_adjustment) {this.rate_adjustment = rate_adjustment;}
    public void changeHotTime(int hot_time) {this.hot_time = hot_time;}
    public void changeContact(String contact) {this.contact = contact;}
    public void changeInfo(String info) {this.info = info;}
    public void changeCaution(String caution) {this.caution = caution;}
    public void changeCourt(Boolean court) {this.court = court;}
    public void changeFacilityStatus(Boolean facility_status) {this.facility_status = facility_status;}
    public void changeFacilityImages(Boolean facility_images) {this.facility_images = facility_images;}
}
