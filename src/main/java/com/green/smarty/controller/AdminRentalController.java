package com.green.smarty.controller;

import com.green.smarty.dto.AdminRentalDTO;
import com.green.smarty.dto.RentalDTO;
import com.green.smarty.service.AdminRentalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/rentals")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
@Log4j2
public class AdminRentalController {
    @Autowired
    private AdminRentalService adminRentalService;

    @GetMapping("/{product_id}")
    public ResponseEntity<List<AdminRentalDTO>> getRentalsByProductId(@PathVariable String product_id) {
        log.info("대여 정보 조회 요청 수신: product_id={}", product_id);
        try {
            List<AdminRentalDTO> rentals = adminRentalService.getRentalsByProductId(product_id);
            if (rentals == null || rentals.isEmpty()) {
                log.warn("대여 정보가 없습니다: product_id={}", product_id);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            log.info("대여 정보 조회 성공: product_id={}", product_id);
            log.debug("조회된 대여 정보: {}", rentals);
            return ResponseEntity.ok(rentals);
        } catch (Exception e) {
            log.error("대여 정보 조회 중 오류 발생: product_id={}", product_id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
