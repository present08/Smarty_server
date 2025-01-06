package com.green.smarty.mapper;

import com.green.smarty.dto.EnrollmentClassDTO;
import com.green.smarty.dto.PaymentDTO;
import com.green.smarty.dto.ScatterDTO;
import com.green.smarty.vo.PaymentVO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Mapper

public interface PaymentMapper {
    void insertPayment(PaymentVO paymentVO);
    int updatePaymentStatus(@Param("payment_id") String payment_id, @Param("status") String status);
    PaymentVO getPaymentById(@Param("payment_id") String payment_id);
    String getMaxPaymentIdForDate(@Param("datePrefix") String datePrefix);
    List<PaymentDTO> getAllPayment();
    // enrollment
    List<EnrollmentClassDTO> getEnrollmentClass();
    void updateEnroll(String enrollment_id);

    // (영준)
    ScatterDTO selectScatter(String payment_id);
}
