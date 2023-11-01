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

  /**
   * 카페에서 주문 거절
   */
  OrderDto rejectOrder(long orderId);

  /**
   * 주문 상태 변경(주문 거절 미포함)
   */
  OrderDto changeOrderStatus(long orderId);

  /**
   * 주문 상태 설명 가져오기
   */
  String findOrderStatusMessage(long orderId);
}
