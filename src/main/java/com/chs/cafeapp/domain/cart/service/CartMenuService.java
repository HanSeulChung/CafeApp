package com.chs.cafeapp.domain.cart.service;

import com.chs.cafeapp.domain.cart.dto.CartMenuChangeQuantity;
import com.chs.cafeapp.domain.cart.dto.CartMenuDto;

public interface CartMenuService {

  /**
   * 장바구니에 담긴 메뉴 전체 삭제
   */
  void deleteAllCartMenu(Long cartId, String userId);

  /**
   * 장바구니에 담긴 특정 메뉴 삭제
   */
  void deleteSpecificCartMenu(Long cartId, Long cartMenuId, String userId);

  /**
   * 장바구니 메뉴 수량 변경
   */
  CartMenuDto changeCartMenuQuantity(CartMenuChangeQuantity cartMenuChangeQuantity, String userId);

}
