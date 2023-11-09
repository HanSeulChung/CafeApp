package com.chs.cafeapp.order.controller;

import com.chs.cafeapp.kafka.service.KafkaProducerService;
import com.chs.cafeapp.order.dto.OrderDto;
import com.chs.cafeapp.order.dto.OrderResponse;
import com.chs.cafeapp.order.service.OrderServiceForAdmin;
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
  private final OrderServiceForAdmin orderServiceForAdmin;
  private final KafkaProducerService producerService;

  /**
   * 전체 주문 조회 Controller
   * @param pageable: defalut -> page = 0, size = 10이고 수정 가능,
   *                + 정렬 기능 >sortBy=정렬할 기준 column-asc
   * @return Slice<OrderDto>: 전체 주문 Slice 반환
   */
  @GetMapping()
  public ResponseEntity<Slice<OrderDto>> viewAllOrders(Pageable pageable) {
    return ResponseEntity.ok(orderServiceForAdmin.viewAllOrders(pageable));
  }

  /**
   * 하루 전체 주문 조회 Controller
   * @param pageable: defalut -> page = 0, size = 10이고 수정 가능,
   *                + 정렬 기능 >sortBy=정렬할 기준 column-asc
   * @return Slice<OrderDto>: 하루 전체 주문 Slice로 반환
   */
  @GetMapping("/days")
  public ResponseEntity<Slice<OrderDto>> viewOrdersDuringDays(Pageable pageable) {
    return ResponseEntity.ok(orderServiceForAdmin.viewAllOrdersDuringDays(pageable));
  }

  /**
   * 일주일 전체 주문 조회 Controller
   * @param pageable: defalut -> page = 0, size = 10이고 수정 가능,
   *                + 정렬 기능 >sortBy=정렬할 기준 column-asc
   * @return Slice<OrderDto>: 일주일 전체 주문 Slice로 반환
   */
  @GetMapping("/weeks")
  public ResponseEntity<Slice<OrderDto>> viewOrdersDuringWeeks(Pageable pageable) {
    return ResponseEntity.ok(orderServiceForAdmin.viewAllOrdersDuringWeeks(pageable));
  }

  /**
   * 한달 전체 주문 조회 Controller
   * @param pageable: defalut -> page = 0, size = 10이고 수정 가능,
   *                + 정렬 기능 >sortBy=정렬할 기준 column-asc
   * @return Slice<OrderDto>: 한달 전체 주문 Slice로 반환
   */
  @GetMapping("/months")
  public ResponseEntity<Slice<OrderDto>> viewOrdersDuringMonths(Pageable pageable) {
    return ResponseEntity.ok(orderServiceForAdmin.viewAllOrdersDuringMonths(pageable));
  }

  /**
   * 3개월 전체 주문 조회 Controller
   * @param pageable: defalut -> page = 0, size = 10이고 수정 가능,
   *                + 정렬 기능 >sortBy=정렬할 기준 column-asc
   * @return Slice<OrderDto>: 3개월 전체 주문 Slice로 반환
   */
  @GetMapping("/three-months")
  public ResponseEntity<Slice<OrderDto>> viewOrdersDuringThreeMonths(Pageable pageable) {
    return ResponseEntity.ok(orderServiceForAdmin.viewAllOrdersDuringThreeMonths(pageable));
  }

  /**
   * 6개월 전체 주문 조회 Controller
   * @param pageable: defalut -> page = 0, size = 10이고 수정 가능,
   *                + 정렬 기능 >sortBy=정렬할 기준 column-asc
   * @return Slice<OrderDto>: 6개월 전체 주문 Slice로 반환
   */
  @GetMapping("/six-months")
  public ResponseEntity<Slice<OrderDto>> viewOrdersDuringSixMonths(Pageable pageable) {
    return ResponseEntity.ok(orderServiceForAdmin.viewAllOrdersDuringSixMonths(pageable));
  }

  /**
   * 1년 전체 주문 조회 Controller
   * @param pageable: defalut -> page = 0, size = 10이고 수정 가능,
   *                + 정렬 기능 >sortBy=정렬할 기준 column-asc
   * @return Slice<OrderDto>: 1년 전체 주문 Slice로 반환
   */
  @GetMapping("/years")
  public ResponseEntity<Slice<OrderDto>> viewOrdersDuringYears(Pageable pageable) {
    return ResponseEntity.ok(orderServiceForAdmin.viewAllOrdersDuringYears(pageable));
  }

  /**
   * 주문 상태별로 주문 전체 조회 Controller
   * @param orderStatusNum: OrderStatus의 num값
   * @param pageable: defalut -> page = 0, size = 10이고 수정 가능,
   *                + 정렬 기능 >sortBy=정렬할 기준 column-asc
   * @return Slice<OrderDto>: 해당 orderStatusNum에 해당되는 주문들 Slice로 반환
   */
  @GetMapping("/order-status/{orderStatusNum}")
  public ResponseEntity<Slice<OrderDto>> viewOrdersByOrderStatus(@PathVariable int orderStatusNum, Pageable pageable) {
    return ResponseEntity.ok(orderServiceForAdmin.viewOrdersByOrderStatus(orderStatusNum, pageable));
  }

  /**
   * 주문 상태별로 주문 하루 전체 조회 Controller
   * @param orderStatusNum: OrderStatus의 num값
   * @param pageable: defalut -> page = 0, size = 10이고 수정 가능,
   *                + 정렬 기능 >sortBy=정렬할 기준 column-asc
   * @return Slice<OrderDto>: 해당 orderStatusNum에 해당되는 주문들 Slice로 반환
   */
  @GetMapping("/order-status/{orderStatusNum}/days")
  public ResponseEntity<Slice<OrderDto>> viewOrdersByOrderStatusDuringDays(@PathVariable int orderStatusNum, Pageable pageable) {
    return ResponseEntity.ok(orderServiceForAdmin.viewOrdersByOrderStatusDuringDays(orderStatusNum, pageable));
  }

  /**
   * 주문 상태별로 주문 일주일 전체 조회 Controller
   * @param orderStatusNum: OrderStatus의 num값
   * @param pageable: defalut -> page = 0, size = 10이고 수정 가능,
   *                + 정렬 기능 >sortBy=정렬할 기준 column-asc
   * @return Slice<OrderDto>: 해당 orderStatusNum에 해당되는 주문들 Slice로 반환
   */
  @GetMapping("/order-status/{orderStatusNum}/weeks")
  public ResponseEntity<Slice<OrderDto>> viewOrdersByOrderStatusDuringWeeks(@PathVariable int orderStatusNum, Pageable pageable) {
    return ResponseEntity.ok(orderServiceForAdmin.viewOrdersByOrderStatusDuringWeeks(orderStatusNum, pageable));
  }

  /**
   * 주문 상태별로 주문 한달 전체 조회 Controller
   * @param orderStatusNum: OrderStatus의 num값
   * @param pageable: defalut -> page = 0, size = 10이고 수정 가능,
   *                + 정렬 기능 >sortBy=정렬할 기준 column-asc
   * @return Slice<OrderDto>: 해당 orderStatusNum에 해당되는 주문들 Slice로 반환
   */
  @GetMapping("/order-status/{orderStatusNum}/months")
  public ResponseEntity<Slice<OrderDto>> viewOrdersByOrderStatusDuringMonths(@PathVariable int orderStatusNum, Pageable pageable) {
    return ResponseEntity.ok(orderServiceForAdmin.viewOrdersByOrderStatusDuringMonths(orderStatusNum, pageable));
  }

  /**
   * 주문 상태별로 주문 3개월 전체 조회 Controller
   * @param orderStatusNum: OrderStatus의 num값
   * @param pageable: defalut -> page = 0, size = 10이고 수정 가능,
   *                + 정렬 기능 >sortBy=정렬할 기준 column-asc
   * @return Slice<OrderDto>: 해당 orderStatusNum에 해당되는 주문들 Slice로 반환
   */
  @GetMapping("/order-status/{orderStatusNum}/three-months")
  public ResponseEntity<Slice<OrderDto>> viewOrdersByOrderStatusDuringThreeMonths(@PathVariable int orderStatusNum, Pageable pageable) {
    return ResponseEntity.ok(orderServiceForAdmin.viewOrdersByOrderStatusDuringThreeMonths(orderStatusNum, pageable));
  }

  /**
   * 주문 상태별로 주문 6개월 전체 조회 Controller
   * @param orderStatusNum: OrderStatus의 num값
   * @param pageable: defalut -> page = 0, size = 10이고 수정 가능,
   *                + 정렬 기능 >sortBy=정렬할 기준 column-asc
   * @return Slice<OrderDto>: 해당 orderStatusNum에 해당되는 주문들 Slice로 반환
   */
  @GetMapping("/order-status/{orderStatusNum}/six-months")
  public ResponseEntity<Slice<OrderDto>> viewOrdersByOrderStatusDuringSixMonths(@PathVariable int orderStatusNum, Pageable pageable) {
    return ResponseEntity.ok(orderServiceForAdmin.viewOrdersByOrderStatusDuringSixMonths(orderStatusNum, pageable));
  }

  /**
   * 주문 상태별로 주문 6개월 전체 조회 Controller
   * @param orderStatusNum: OrderStatus의 num값
   * @param pageable: defalut -> page = 0, size = 10이고 수정 가능,
   *                + 정렬 기능 >sortBy=정렬할 기준 column-asc
   * @return Slice<OrderDto>: 해당 orderStatusNum에 해당되는 주문들 Slice로 반환
   */
  @GetMapping("/order-status/{orderStatusNum}/years")
  public ResponseEntity<Slice<OrderDto>> viewOrdersByOrderStatusDuringYears(@PathVariable int orderStatusNum, Pageable pageable) {
    return ResponseEntity.ok(orderServiceForAdmin.viewOrdersByOrderStatusDuringYears(orderStatusNum, pageable));
  }

  /**
   * 주문 거절 Controller
   * @param orderId: 가게에서 거절할 주문 id
   * @return OrderResponse: 거절한 order id와 message
   */
  @PatchMapping("/rejections/{orderId}")
  public ResponseEntity<OrderResponse> rejectOrderStatus(@PathVariable long orderId) {
    OrderDto orderDto = orderServiceForAdmin.rejectOrder(orderId);
    return ResponseEntity.ok(OrderResponse.toResponse(orderDto, orderDto.getOrderStatus().getDescription()));
  }

  /**
   * 상태 변경 Controller
   * @param orderId: 상태 변경할 주문 id
   * @return OrderResponse: 상태 변경된 order id와 message
   */
  @PatchMapping("/order-status/{orderId}")
  public ResponseEntity<OrderResponse> changeOrderStatus(@PathVariable long orderId) {

    OrderDto orderDto = orderServiceForAdmin.changeOrderStatus(orderId);
    String message = orderServiceForAdmin.viewMessageChanges(orderId);
    OrderResponse response = OrderResponse.toResponse(orderDto, message);
    producerService.sendMessage(orderId, orderDto.getOrderStatus().getStatusName());
    return ResponseEntity.ok(response);
  }  
}