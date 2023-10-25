package com.chs.cafeapp.cart.service;

import com.chs.cafeapp.cart.dto.CartInput;
import com.chs.cafeapp.cart.dto.CartMenuDto;
import java.util.List;

public interface CartService {

  /**
   * 장바구니 추가
   */
  CartMenuDto addCart(CartInput cartInput, String userId);

  /**
   * 장바구니 조회
   */
  List<CartMenuDto> viewAllCartMenuInCart(String userId);
}
