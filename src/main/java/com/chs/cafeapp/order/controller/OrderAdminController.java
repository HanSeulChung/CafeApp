package com.chs.cafeapp.order.controller;

import com.chs.cafeapp.order.dto.OrderDto;
import com.chs.cafeapp.order.dto.OrderResponse;
import com.chs.cafeapp.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin 주문 Controller
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/orders")
public class OrderAdminController {
  private final OrderService orderService;

  /**
   * 주문 거절 Controller
   * @param orderId: 가게에서 거절할 주문 id
   * @return OrderResponse: 거절한 order id와 message
   */
  @PatchMapping("/rejections/{orderId}")
  public ResponseEntity<OrderResponse> rejectOrderStatus(@PathVariable long orderId) {
    OrderDto orderDto = orderService.rejectOrder(orderId);
    String message = "카페에서 주문이 거절되었습니다.";
    return ResponseEntity.ok(OrderResponse.toResponse(orderDto, message));
  }

  /**
   * 상태 변경 Controller
   * @param orderId: 상태 변경할 주문 id
   * @return OrderResponse: 상태 변경된 order id와 message
   */
  @PatchMapping("/order-status/{orderId}")
  public ResponseEntity<OrderResponse> changeOrderStatus(@PathVariable long orderId) {
    String message = orderService.findOrderStatusMessage(orderId);
    OrderDto orderDto = orderService.changeOrderStatus(orderId);
    message += " 상태에서 " + orderService.findOrderStatusMessage(orderId) + " 상태로 변경되었습니다.";

    return ResponseEntity.ok(OrderResponse.toResponse(orderDto, message));
  }
}
