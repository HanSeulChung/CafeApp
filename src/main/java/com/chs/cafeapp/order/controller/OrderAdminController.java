package com.chs.cafeapp.order.controller;

import com.chs.cafeapp.kafka.service.KafkaProducerService;
import com.chs.cafeapp.order.dto.OrderDto;
import com.chs.cafeapp.order.dto.OrderResponse;
import com.chs.cafeapp.order.service.OrderService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
  private final KafkaProducerService producerService;

  /**
   * 전체 주문 조회 Controller
   * @return List<OrderDto>: 전체 주문 List로 반환
   */
  @GetMapping()
  public ResponseEntity<Slice<OrderDto>> viewAllOrders(Pageable pageable) {
    return ResponseEntity.ok(orderService.viewAllOrders(pageable));
  }

  /**
   * 주문 상태별로 주문 조회 Controller
   * @param orderStatusNum: OrderStatus의 num값
   * @return List<OrderDto>: 해당 orderStatusNum에 해당되는 주문들 List로 반환
   */
  @GetMapping("/order-status/{orderStatusNum}")
  public ResponseEntity<Slice<OrderDto>> viewOrdersByOrderStatus(@PathVariable int orderStatusNum, Pageable pageable) {
    return ResponseEntity.ok(orderService.viewOrdersByOrderStatus(orderStatusNum, pageable));
  }
  
  /**
   * 주문 거절 Controller
   * @param orderId: 가게에서 거절할 주문 id
   * @return OrderResponse: 거절한 order id와 message
   */
  @PatchMapping("/rejections/{orderId}")
  public ResponseEntity<OrderResponse> rejectOrderStatus(@PathVariable long orderId) {
    OrderDto orderDto = orderService.rejectOrder(orderId);
    return ResponseEntity.ok(OrderResponse.toResponse(orderDto, orderDto.getOrderStatus().getDescription()));
  }

  /**
   * 상태 변경 Controller
   * @param orderId: 상태 변경할 주문 id
   * @return OrderResponse: 상태 변경된 order id와 message
   */
  @PatchMapping("/order-status/{orderId}")
  public ResponseEntity<OrderResponse> changeOrderStatus(@PathVariable long orderId) {

    OrderDto orderDto = orderService.changeOrderStatus(orderId);
    String message = orderService.viewMessageChanges(orderId);
    OrderResponse response = OrderResponse.toResponse(orderDto, message);
    producerService.sendMessage(orderId, orderDto.getOrderStatus().getStatusName());
    return ResponseEntity.ok(response);
  }  
}