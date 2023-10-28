package com.chs.cafeapp.cart.service;

import com.chs.cafeapp.cart.dto.CartMenuDto;
import com.chs.cafeapp.cart.dto.CartMenuQuantityAdd;
import com.chs.cafeapp.cart.dto.CartMenuQuantityMinus;

public interface CartMenuService {

  /**
   * 장바구니에 담긴 메뉴 전체 삭제
   */
  void deleteAllCartMenu(Long cartId, String userId);

  /**
   * 장바구니에 담긴 특정 메뉴 삭제
   */
  void deleteSpecificCartMenu(Long cartMenuId, String userId);

  /**
   * 장바구니 메뉴 수량 증가
   */
  CartMenuDto addCartMenuQuantity(CartMenuQuantityAdd cartMenuQuantityAdd, String userId);
  /**
   * 장바구니 메뉴 수량 감소
   */
  CartMenuDto minusCartMenuQuantity(CartMenuQuantityMinus cartMenuQuantityMinus, String userId);
}
