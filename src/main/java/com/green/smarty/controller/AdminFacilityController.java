package com.green.smarty.controller;

import com.green.smarty.service.AdminFacilityService;
import com.green.smarty.vo.FacilityVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/facilities")
public class AdminFacilityController {

    @Autowired
    private AdminFacilityService adminFacilityService;

    // Create (시설 등록)
    @PostMapping("/")
    public String register(@ModelAttribute FacilityVO facilityVO) throws IOException {
        System.out.println("시설 등록! facilityVO = " + facilityVO);
        String id = adminFacilityService.register(facilityVO);
        System.out.println("등록된 시설 id = " + id + ", facilityDTO = " + facilityVO);
        return id;
    }

    // Read (시설 조회)
    @GetMapping("/list")
    public List<FacilityVO> getList() {
        List<FacilityVO> list = adminFacilityService.getList();
        System.out.println("시설 전체 조회! : " + list);
        return list;
    }

    @GetMapping("/{facility_id}")
    public FacilityVO read(@PathVariable(name = "facility_id") String facility_id) {
        System.out.println("시설 하나 조회! facility_id = " + facility_id);
        return adminFacilityService.read(facility_id);
    }

    @GetMapping("/images/{file_name}")
    public ResponseEntity<Resource> showImages(@PathVariable(name = "file_name") String file_name) {
        System.out.println("시설 이미지 조회! file_name = " + file_name);
        return adminFacilityService.showImages(file_name);
    }

    // Update (시설 수정)
    @PutMapping("/{facility_id}")
    public String modify(
            @PathVariable(name = "facility_id") String facility_id,
            @ModelAttribute FacilityVO facilityVO) {
        System.out.println("시설 수정! facility_id = " + facility_id);
        adminFacilityService.modify(facility_id, facilityVO);
        return "시설 수정 완료";
    }

    // Delete (시설 삭제)
    @DeleteMapping("/{facility_id}")
    public String remove(@PathVariable(name = "facility_id") String facility_id) {
        System.out.println("시설 삭제! facility_id = " + facility_id);
        adminFacilityService.remove(facility_id);
        return "해당 시설이 삭제되었습니다.";
    }

}
