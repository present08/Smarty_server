package com.green.smarty.controller;

import com.green.smarty.service.AdminCourtService;
import com.green.smarty.vo.CourtVO;
import com.green.smarty.vo.FacilityVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/courts")
public class AdminCourtController {
    @Autowired
    private AdminCourtService adminCourtService;

    // Create
    @PostMapping("/")
    public List<String> register(@RequestBody List<CourtVO> courtList) {
        System.out.println("코트 등록 리스트! courtList = " + courtList);
        List<String> courtIdList = adminCourtService.register(courtList);
        return courtIdList;
    }

    // Read
    // 선택 시설의 코트 전체 조회
    @GetMapping("/list/{facility_id}")
    public List<CourtVO> getList(@PathVariable (name = "facility_id") String facility_id) {
        System.out.println("코트 전체 조회! facility_id = " + facility_id);
        List<CourtVO> list = adminCourtService.getList(facility_id);
        return list;
    }

    // 코트 하나 조회
    @GetMapping("/{court_id}")
    public CourtVO read(@PathVariable (name = "court_id") String court_id) {
        System.out.println("코트 하나 조회! court_id = " + court_id);
        return adminCourtService.read(court_id);
    }

    // Update
    @PutMapping("/{facility_id}")
    public String modify(
            @PathVariable(name = "facility_id") String facility_id,
            @RequestBody List<CourtVO> courtList) {
        adminCourtService.modify(facility_id, courtList);
        return "코트 수정 성공";
    }

    // Delete
    @DeleteMapping("/{court_id}")
    public String remove(@PathVariable(name = "court_id") String court_id) {
        System.out.println("코트 삭제! court_id = " + court_id);
        adminCourtService.remove(court_id);
        return "코트 및 예약내역이 삭제되었습니다.";
    }
}
