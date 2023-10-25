package com.chs.cafeapp.cart.service;

public interface CartMenuService {

  /**
   * 장바구니에 담긴 메뉴 전체 삭제
   */
  void deleteAllCartMenu(Long cartId, String userId);

  /**
   * 장바구니에 담긴 특정 메뉴 삭제
   */
  void deleteSpecificCartMenu(Long cartMenuId, String userId);
}
