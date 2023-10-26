package com.chs.cafeapp.order.service;

import com.chs.cafeapp.order.dto.OrderAllFromCartInput;
import com.chs.cafeapp.order.dto.OrderDto;
import com.chs.cafeapp.order.dto.OrderFromCartInput;
import com.chs.cafeapp.order.dto.OrderInput;

public interface OrderService {
  /**
   * 개별 상품 주문
   */
  OrderDto orderIndividualMenu(OrderInput orderInput, String userId);

  /**
   * 장바구니에서 여러 상품 주문(선택)
   */
  OrderDto orderFromCart(OrderFromCartInput orderFromCartInput, String userId);

  /**
   * 장바구니 메뉴 전체 주문
   */
  OrderDto orderAllFromCart(OrderAllFromCartInput orderAllFromCartInput, String userId);

}
