package com.green.smarty.controller;

import com.green.smarty.dto.ClassAdminDTO;
import com.green.smarty.service.AdminClassService;
import com.green.smarty.vo.ClassDetailVO;
import com.green.smarty.vo.ClassVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/classes")
public class AdminClassController {
    @Autowired
    private AdminClassService adminClassService;

    // Create
    @PostMapping("/")
    public String register(@RequestBody List<ClassAdminDTO> classList) {
        System.out.println("클래스 등록 classList = " + classList);
        adminClassService.register(classList);
        return "클래스 등록 성공";
    }

    // Read
    // 선택한 시설 관련 강의 전체 조회
    @GetMapping("/list/{facility_id}")
    public List<ClassVO> getList(@PathVariable(name = "facility_id") String facility_id) {
        System.out.println("클래스 전체 조회! facility_id = " + facility_id);
        List<ClassVO> list = adminClassService.getList(facility_id);
        return list;
    }

    // 강의 하나 조회
    @GetMapping("/{class_id}")
    public ClassVO read(@PathVariable (name = "class_id") String class_id) {
        System.out.println("클래스 하나 조회! class_id = " + class_id);
        System.out.println("강의 조회" + adminClassService.read(class_id));
        return adminClassService.read(class_id);
    }
    // 선택 강의의 상세 내용 조회
    @GetMapping("/detailList/{class_id}")
    public List<ClassDetailVO> getDetailList(@PathVariable(name = "class_id") String class_id) {
        System.out.println("클래스 상세 조회! class_id = " + class_id);
        List<ClassDetailVO> list = adminClassService.getDetailList(class_id);
        return list;
    }

    //Update
    @PutMapping("/{class_id}")
    public String modify(
            @PathVariable(name = "class_id") String class_id,
            @RequestBody ClassAdminDTO classAdminDTO) {
        System.out.println("클래스 수정! class_id = " + class_id);
        System.out.println("클래스 수정! classAdminDTO = " + classAdminDTO);
        adminClassService.modify(class_id, classAdminDTO);
        return "클래스 수정 성공";
    }

    // Delete
    @DeleteMapping("/{class_id}")
    public String remove(@PathVariable(name = "class_id") String class_id) {
        System.out.println("클래스 삭제! class_id = " + class_id);
        adminClassService.remove(class_id);
        return "강의가 삭제되었습니다.";
    }
}
