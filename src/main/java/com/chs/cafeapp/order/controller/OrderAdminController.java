package com.chs.cafeapp.order.controller;

import com.chs.cafeapp.order.dto.OrderDto;
import com.chs.cafeapp.order.service.OrderService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
   * 전체 주문 조회 Controller
   * @return List<OrderDto>: 전체 주문 List로 반환
   */
  @GetMapping()
  public ResponseEntity<List<OrderDto>> viewAllOrders() {
    return ResponseEntity.ok(orderService.viewAllOrders());
  }

  /**
   * 주문 상태별로 주문 조회 Controller
   * @param orderStatusNum: OrderStatus의 num값
   * @return List<OrderDto>: 해당 orderStatusNum에 해당되는 주문들 List로 반환
   */
  @GetMapping("/order-status/{orderStatus}")
  public ResponseEntity<List<OrderDto>> viewOrdersByOrderStatus(@PathVariable int orderStatusNum) {
    return ResponseEntity.ok(orderService.viewOrdersByOrderStatus(orderStatusNum));
  }
}
