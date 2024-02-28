package com.chs.cafeapp.domain.order.service;

import com.chs.cafeapp.domain.order.dto.OrderAllFromCartInput;
import com.chs.cafeapp.domain.order.dto.OrderDto;
import com.chs.cafeapp.domain.order.dto.OrderFromCartInput;
import com.chs.cafeapp.domain.order.dto.OrderInput;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface OrderMemberService {
  /**
   * 개별 상품 주문
   */
  OrderDto orderIndividualMenu(OrderInput orderInput, String memberId);

  /**
   * 장바구니에서 여러 상품 주문(선택)
   */
  OrderDto orderFromCart(OrderFromCartInput orderFromCartInput, String memberId);

  /**
   * 장바구니 메뉴 전체 주문
   */
  OrderDto orderAllFromCart(OrderAllFromCartInput orderAllFromCartInput, String memberId);


  /**
   * 주문 전체 조회 (for user)
   */
  Slice<OrderDto> viewAllOrders(String memberId, Pageable pageable);

  /**
   * 하루 전체 주문 조회(for user)
   */
  Slice<OrderDto> viewAllOrdersDuringDays(String memberId, Pageable pageable);

  /**
   * 일주일 전체 주문 조회(for user)
   */
  Slice<OrderDto> viewAllOrdersDuringWeeks(String memberId, Pageable pageable);

  /**
   * 1개월 전체 주문 조회(for user)
   */
  Slice<OrderDto> viewAllOrdersDuringMonths(String memberId, Pageable pageable);

  /**
   * 3개월 전체 주문 조회(for user)
   */
  Slice<OrderDto> viewAllOrdersDuringThreeMonths(String memberId, Pageable pageable);

  /**
   * 6개월 전체 주문 조회(for user)
   */
  Slice<OrderDto> viewAllOrdersDuringSixMonths(String memberId, Pageable pageable);

  /**
   * 1년 전체 주문 조회(for user)
   */
  Slice<OrderDto> viewAllOrdersDuringYears(String memberId, Pageable pageable);

  /**
   * 사용자가 결제 취소(주문 취소)
   */
  OrderDto cancelOrder(long orderId, String memberId);
}
