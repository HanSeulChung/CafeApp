package com.chs.cafeapp.cart.service;

import com.chs.cafeapp.cart.dto.CartInput;
import com.chs.cafeapp.cart.dto.CartMenuDto;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CartService {

  /**
   * 장바구니 추가
   */
  CartMenuDto addCart(CartInput cartInput, String userId);

  /**
   * 장바구니 조회
   */
  Page<CartMenuDto> viewAllCartMenuInCart(String userId, Pageable pageable);
}
