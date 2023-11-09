package com.chs.cafeapp.order.service;

import com.chs.cafeapp.order.dto.OrderAllFromCartInput;
import com.chs.cafeapp.order.dto.OrderDto;
import com.chs.cafeapp.order.dto.OrderFromCartInput;
import com.chs.cafeapp.order.dto.OrderInput;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

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
   * 주문 전체 조회 (for admin)
   */
  Slice<OrderDto> viewAllOrders(Pageable pageable);

  /**
   * orderStatus에 따른 주문 조회
   */
  Slice<OrderDto> viewOrdersByOrderStatus(int orderStatus, Pageable pageable);
  
  /**
   * 카페에서 주문 거절
   */
  OrderDto rejectOrder(long orderId);

  /**
   * 주문 상태 변경(주문 거절 미포함)
   */
  OrderDto changeOrderStatus(long orderId);

  /**
   * 주문 상태 변경 message
   */
  String viewMessageChanges(long orderId);

  /**
   * 사용자가 결제 취소(주문 취소)
   */
  OrderDto cancelOrder(long orderId, String userId);
}
