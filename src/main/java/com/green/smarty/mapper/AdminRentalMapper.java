package com.green.smarty.mapper;

import com.green.smarty.dto.AdminRentalDTO;
import com.green.smarty.dto.RentalDTO;
import com.green.smarty.vo.RentalVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminRentalMapper {
    List<AdminRentalDTO> getRentalsByProductId(@Param("product_id") String product_id);

    AdminRentalDTO getRentalById(@Param("rental_id") String rental_id);

    void updateRentalStatus(@Param("rental_id") String rental_id, @Param("status") boolean status);

    void decrementStock(@Param("product_id") String product_id, @Param("count") int count);

    void incrementStock(@Param("product_id") String product_id, @Param("count") int count);
}
