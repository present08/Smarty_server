package com.green.smarty.mapper;

import com.green.smarty.dto.ProductAdminDTO;
import com.green.smarty.vo.ProductVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AdminProductMapper {
    void insertProduct(ProductVO vo);

    void fileUpload(@Param("product_id") String productId,
                    @Param("origin_path") String originPath,
                    @Param("thumbnail_path") String thumbnailPath,
                    @Param("file_name") String fileName);    List<ProductVO> getAllProducts();

    List<ProductVO> getProductsByFacilityId(@Param("facility_id") String facilityId);

    ProductVO getProductById(@Param("product_id") String product_id);

    List<String> getProductImages(@Param("product_id") String product_id);

    void deleteProductImage(@Param("product_id") String product_id, @Param("file_name") String file_name);

    boolean existsByProductId(String product_id);
}
