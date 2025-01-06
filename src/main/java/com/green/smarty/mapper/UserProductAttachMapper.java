package com.green.smarty.mapper;


import com.green.smarty.vo.ProductAttachVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper

public interface UserProductAttachMapper {
    void insert(ProductAttachVO productAttachVO);
    List<ProductAttachVO> findByProductId(String product_id);
    void deleteByProductId(String product_id);
}
