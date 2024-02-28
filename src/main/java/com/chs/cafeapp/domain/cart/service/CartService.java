package com.chs.cafeapp.domain.cart.service;

import com.chs.cafeapp.domain.cart.dto.CartInput;
import com.chs.cafeapp.domain.cart.dto.CartMenuDto;
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
