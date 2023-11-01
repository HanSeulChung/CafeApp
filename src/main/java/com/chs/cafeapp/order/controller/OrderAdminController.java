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

  @GetMapping()
  public ResponseEntity<List<OrderDto>> viewAllOrders() {
    return ResponseEntity.ok(orderService.viewAllOrders());
  }

  @GetMapping("/order-status/{orderStatus}")
  public ResponseEntity<List<OrderDto>> viewOrdersByOrderStatus(@PathVariable int orderStatus) {
    return ResponseEntity.ok(orderService.viewOrdersByOrderStatus(orderStatus));
  }
}
