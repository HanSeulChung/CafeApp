package com.chs.cafeapp.order.service;

import com.chs.cafeapp.order.dto.OrderDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface OrderAdminService {
  /**
   * 주문 전체 조회
   */
  Slice<OrderDto> viewAllOrders(Pageable pageable);

  /**
   * 하루 전체 주문 조회
   */
  Slice<OrderDto> viewAllOrdersDuringDays(Pageable pageable);

  /**
   * 일주일 전체 주문 조회
   */
  Slice<OrderDto> viewAllOrdersDuringWeeks(Pageable pageable);

  /**
   * 1개월 전체 주문 조회
   */
  Slice<OrderDto> viewAllOrdersDuringMonths(Pageable pageable);

  /**
   * 3개월 전체 주문 조회
   */
  Slice<OrderDto> viewAllOrdersDuringThreeMonths(Pageable pageable);

  /**
   * 6개월 전체 주문 조회
   */
  Slice<OrderDto> viewAllOrdersDuringSixMonths(Pageable pageable);

  /**
   * 1년 전체 주문 조회
   */
  Slice<OrderDto> viewAllOrdersDuringYears(Pageable pageable);

  /**
   * orderStatus에 따른 주문 전체 조회
   */
  Slice<OrderDto> viewOrdersByOrderStatus(int orderStatus, Pageable pageable);

  /**
   * orderStatus에 따른 주문 하루 전체 조회
   */
  Slice<OrderDto> viewOrdersByOrderStatusDuringDays(int orderStatus, Pageable pageable);

  /**
   * orderStatus에 따른 주문 일주일 전체 조회
   */
  Slice<OrderDto> viewOrdersByOrderStatusDuringWeeks(int orderStatus, Pageable pageable);

  /**
   * orderStatus에 따른 주문 한달 전체 조회
   */
  Slice<OrderDto> viewOrdersByOrderStatusDuringMonths(int orderStatus, Pageable pageable);

  /**
   * orderStatus에 따른 주문 3개월 전체 조회
   */
  Slice<OrderDto> viewOrdersByOrderStatusDuringThreeMonths(int orderStatus, Pageable pageable);

  /**
   * orderStatus에 따른 주문 6개월 전체 조회
   */
  Slice<OrderDto> viewOrdersByOrderStatusDuringSixMonths(int orderStatus, Pageable pageable);

  /**
   * orderStatus에 따른 주문 1년 전체 조회
   */
  Slice<OrderDto> viewOrdersByOrderStatusDuringYears(int orderStatus, Pageable pageable);

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

}
