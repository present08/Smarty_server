package com.green.smarty;

import com.green.smarty.mapper.UserProductMapper;
import com.green.smarty.vo.FacilityVO;
import com.green.smarty.vo.ProductVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

@SpringBootTest

public class ProductTests {
    @Autowired
    private UserProductMapper userProductMapper;

    @Test
    public void insertProduct() {
        List<String> p_arr = Arrays.asList(new String[] { "물품1", "물품2", "물품3", "물품4", "물품5", "물품6", "물품7", "물품8" });
        List<String> f_arr = Arrays.asList(new String[] { "fc_1731986897375", "fc_1731986897388" });
        List<String> s_arr = Arrays.asList(new String[] { "S", "M", "L", "XL", "XXL" });
        int cnt = 0;
        for (String i : f_arr) {
            for (String j : p_arr){
            String id = "p_"+ i.substring(12)+String.format("%03d",cnt+1);
            int randomIndex = (int)(Math.random()* s_arr.size());
            ProductVO vo = ProductVO.builder()
                    .product_id(id)
                    .facility_id(i)
                    .product_name(j)
                    .price((int)((Math.random()*100)+1000)*100)
                    .size(s_arr.get(randomIndex))
                    .stock(50)
                    .build();
            int result = userProductMapper.insertProduct(vo);
            cnt++;
            System.out.println(result);
            }
        }
    }
}
