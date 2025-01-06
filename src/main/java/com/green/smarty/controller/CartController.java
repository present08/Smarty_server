package com.green.smarty.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.green.smarty.dto.CartDTO;
import com.green.smarty.dto.UserCartListDTO;
import com.green.smarty.mapper.PublicMapper;
import com.green.smarty.mapper.UserMapper;
import com.green.smarty.mapper.UserProductMapper;
import com.green.smarty.mapper.UserRentalMapper;
import com.green.smarty.service.CartService;
import com.green.smarty.service.SendEmailService;
import com.green.smarty.service.UserRentalService;
import com.green.smarty.vo.CartVO;
import com.green.smarty.vo.RentalVO;

@RestController
@RequestMapping("/api/user/cart")

public class CartController {
    @Autowired
    private CartService cartService;

    @Autowired
    private UserRentalMapper userRentalMapper;

    @Autowired
    private UserRentalService userRentalService;

    @Autowired
    private PublicMapper publicMapper;

    // 영준
    @Autowired
    private SendEmailService sendEmailService;
    @Autowired
    private UserProductMapper userProductMapper;
    @Autowired
    private UserMapper userMapper;

    @GetMapping("/{user_id}")
    public ResponseEntity<List<UserCartListDTO>> getCartItem(@PathVariable String user_id) {
        List<UserCartListDTO> cartItems = cartService.getUserCart(user_id);
        return ResponseEntity.ok(cartItems);
    }

    @PostMapping("/rentals")
    public ResponseEntity<String> cartRental(@RequestBody List<CartDTO> cartDTOList) {
        try {
            for (CartDTO cartDTO : cartDTOList) {
                LocalDateTime date = LocalDateTime.now();
                List<RentalVO> rentalVOList = publicMapper.getRentalAll();
                List<RentalVO> rentalList = new ArrayList<>();

                for (RentalVO item : rentalVOList) {
                    String itemDate = item.getRental_id().substring(2, 10);
                    if (itemDate.equals("" + date.getYear() + date.getMonthValue() +
                            (date.getDayOfMonth() < 10 ? "0" + date.getDayOfMonth() : date.getDayOfMonth()))) {
                        rentalList.add(item);
                    }
                }

                String rentalId = "R_" + date.getYear() + date.getMonthValue() +
                        (date.getDayOfMonth() < 10 ? "0" + date.getDayOfMonth() : date.getDayOfMonth()) +
                        String.format("%03d", rentalList.size() + 1);

                RentalVO rental = RentalVO.builder()
                        .rental_id(rentalId) // rental_id 설정
                        .user_id(cartDTO.getUser_id())
                        .product_id(cartDTO.getProduct_id())
                        .rental_date(LocalDateTime.now())
                        .count(cartDTO.getQuantity())
                        .return_date(LocalDateTime.now().plusDays(1)) // 기본 반납일 (1일 뒤)
                        .rental_status(true)
                        .build();

                userRentalService.insertRental(rental, cartDTO.getQuantity());
            }

            return ResponseEntity.ok("Rental 데이터 삽입 완료");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Rental 데이터 삽입 실패: " + e.getMessage());
        }
    }

    @PostMapping("/")
    public ResponseEntity<List<String>> addCartItem(@RequestBody List<CartDTO> cartDTOList) {
        List<String> createdIds = new ArrayList<>();
        for (CartDTO cartDTO : cartDTOList) {
            LocalDateTime date = LocalDateTime.now();
            String formatDate = date.getYear() + String.format("%02d", date.getMonthValue())
                    + String.format("%02d", date.getDayOfMonth());

            List<CartVO> allCart = cartService.getAllCart();
            List<CartVO> cartList = new ArrayList<>();

            for (CartVO item : allCart) {
                String cartDate = item.getCart_id().substring(2, 10);
                if (cartDate.equals(formatDate)) {
                    cartList.add(item);
                }
            }

            String id = "C_" + formatDate + String.format("%03d", cartList.size() + 1);

            CartVO cartVO = CartVO.builder()
                    .cart_id(id)
                    .user_id(cartDTO.getUser_id())
                    .product_id(cartDTO.getProduct_id())
                    .quantity(cartDTO.getQuantity())
                    .created_at(LocalDateTime.now())
                    .build();

            cartService.addCart(cartVO);
            createdIds.add(id);
        }

        return ResponseEntity.ok(createdIds);
    }

    @PutMapping("/")
    public ResponseEntity<String> updateCartItem(@RequestBody Map<String, Object> cart) {
        CartVO cartVO = CartVO.builder()
                .cart_id(cart.get("cart_id").toString())
                .quantity(Integer.parseInt(cart.get("quantity").toString()))
                .build();
        cartService.updateCart(cartVO);
        return ResponseEntity.ok("장바구니 업데이트");
    }

    @DeleteMapping("/{cart_id}")
    public ResponseEntity<String> removeCartItem(@PathVariable String cart_id) {
        cartService.removeCart(cart_id);
        return ResponseEntity.ok("카트 아이디로 삭제");
    }

    @DeleteMapping("/clear/{user_id}")
    public ResponseEntity<String> clearCart(@PathVariable String user_id) {
        cartService.clearCart(user_id);
        System.out.println("============================="+user_id);
        return ResponseEntity.ok("장바구니 전부 비우기");
    }

}
