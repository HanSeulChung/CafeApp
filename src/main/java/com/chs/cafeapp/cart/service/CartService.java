package com.chs.cafeapp.cart.service;

import com.chs.cafeapp.cart.dto.CartInput;
import com.chs.cafeapp.cart.dto.CartMenuDto;
import com.chs.cafeapp.cart.dto.CartResponse;

public interface CartService {

  /**
   * 장바구니 추가
   */
  CartMenuDto addOrder(CartInput cartInput, String userId);
}
