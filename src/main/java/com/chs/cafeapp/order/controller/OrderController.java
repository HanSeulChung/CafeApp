package com.chs.cafeapp.order.controller;


import com.chs.cafeapp.order.dto.OrderDto;
import com.chs.cafeapp.order.dto.OrderFromCartInput;
import com.chs.cafeapp.order.dto.OrderInput;
import com.chs.cafeapp.order.dto.OrderResponse;
import com.chs.cafeapp.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * 주문 Controller
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {
  private final OrderService orderService;
  /**
   * 개별 주문 생성
   */
  @PostMapping()
  public OrderResponse addOrder(@RequestBody OrderInput request, @RequestParam String userId) {
    OrderDto orderDto = orderService.orderIndividualMenu(request, userId);
    return OrderResponse.toResponse(orderDto);
  }

  /**
   * 장바구니에서 주문 생성
   */
  @PostMapping("/shopping-basket")
  public OrderResponse addOrderFromBasket(@RequestBody OrderFromCartInput request) {

    return OrderResponse.builder().build();
  }

}