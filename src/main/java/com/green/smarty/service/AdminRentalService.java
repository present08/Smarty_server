package com.green.smarty.service;

import com.green.smarty.dto.AdminRentalDTO;
import com.green.smarty.mapper.AdminProductMapper;
import com.green.smarty.mapper.AdminRentalMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminRentalService {
    @Autowired
    private AdminRentalMapper adminRentalMapper;

    @Autowired
    private AdminProductMapper adminProductMapper;

    public List<AdminRentalDTO> getRentalsByProductId(String product_id) {
        List<AdminRentalDTO> rentals = adminRentalMapper.getRentalsByProductId(product_id);
        if (rentals == null || rentals.isEmpty()) {
            throw new IllegalArgumentException("해당 product_id에 대한 대여 데이터가 없습니다.");
        }
        return rentals;
    }
}
