package com.chs.cafeapp.order.controller;


import com.chs.cafeapp.order.dto.OrderAllFromCartInput;
import com.chs.cafeapp.order.dto.OrderDto;
import com.chs.cafeapp.order.dto.OrderFromCartInput;
import com.chs.cafeapp.order.dto.OrderInput;
import com.chs.cafeapp.order.dto.OrderResponse;
import com.chs.cafeapp.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.chs.cafeapp.exception.CustomException;

/**
 * 주문 Controller
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {
  private final OrderService orderService;


  /**
   * 개별 주문 생성(수정 필요
   * @param request
   * @param userId
   * @return
   * @throws CustomException:
   */
  @PostMapping()
  public OrderResponse addOrder(@RequestBody OrderInput request, @RequestParam String userId) {
    OrderDto orderDto = orderService.orderIndividualMenu(request, userId);
    return OrderResponse.toResponse(orderDto);
  }


  /**
   * 장바구니에서 주문 생성(선택 주문 가능)
   * @param request
   * @param userId
   * @return
   * @throws CustomException
   */
  @PostMapping("/shopping-basket/select-items/{selectItemCount}")
  public OrderResponse addOrderFromBasket(@PathVariable int selectItemCount, @RequestBody OrderFromCartInput request, @RequestParam String userId) {
    OrderDto orderDto = orderService.orderFromCart(request, userId);
    return OrderResponse.toResponse(orderDto);
  }

  /**
   * 장바구니에서 주문 생성(장바구니 메뉴 전체 주문)
   */
  @PostMapping("/shopping-basket")
  public OrderResponse addOrderFromBasket(@RequestBody OrderAllFromCartInput reqeust, @RequestParam String userId) {
    OrderDto orderDto = orderService.orderAllFromCart(reqeust, userId);
    return OrderResponse.toResponse(orderDto);
  }
}
