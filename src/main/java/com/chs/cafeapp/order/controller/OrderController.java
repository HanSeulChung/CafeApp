package com.chs.cafeapp.order.controller;


import com.chs.cafeapp.order.dto.OrderAllFromCartInput;
import com.chs.cafeapp.order.dto.OrderDto;
import com.chs.cafeapp.order.dto.OrderFromCartInput;
import com.chs.cafeapp.order.dto.OrderInput;
import com.chs.cafeapp.order.dto.OrderResponse;
import com.chs.cafeapp.order.service.OrderService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.chs.cafeapp.exception.CustomException;
import org.w3c.dom.stylesheets.LinkStyle;

/**
 * 주문 Controller
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {
  private final OrderService orderService;


  /**
   * 개별 주문 생성
   * @param request: 메뉴 조회시 바로 주문 OrderInput> menuId, menuName, menuPrice, quantity, coupon 사용 여부
   * @param userId: 사용자 loginId
   * @return OrderResponse: 주문 id와 주문상태 message
   * @throws CustomException: 사용자 loginId가 없을 때, menu id를 가진 menu가 존재하지 않을 때,
   *                          orderInput의 menuName과, menu의 menuName이 일치하지않을 때 CustomException 발생
   */
  @PostMapping()
  public OrderResponse addOrder(@RequestBody OrderInput request, @RequestParam String userId) {
    OrderDto orderDto = orderService.orderIndividualMenu(request, userId);
    return OrderResponse.toResponse(orderDto, orderDto.getOrderStatus().getDescription());
  }


  /**
   * 장바구니에서 주문 생성: 선택 주문
   * @param request: cartId, 장바구니에서 선택된 장바구니 메뉴들(idList={1, 2}), coupon 사용 여부
   * @param userId: 사용자 loginId
   * @return OrderResponse: 주문 id와 주문상태 message
   * @throws CustomException: 사용자 loginId가 없을 때, 장바구니 id가 존재하지 않을 때
   *                          menu id를 가진 menu가 존재하지 않을 때, 장바구니에 아무것도 존재하지 않을 때
   *                         장바구니에 있는 메뉴 id가 존재하지 않을 때,
   *                         메뉴의 재고 이상 주문하려고 할 때 CustomException 발생
   */
  @PostMapping("/shopping-basket/select-items/{selectItemCount}")
  public OrderResponse addOrderFromBasket(@PathVariable int selectItemCount, @RequestBody OrderFromCartInput request, @RequestParam String userId) {
    OrderDto orderDto = orderService.orderFromCart(request, userId);
    return OrderResponse.toResponse(orderDto, orderDto.getOrderStatus().getDescription());
  }

  /**
   * 장바구니에서 주문 생성: 장바구니 메뉴 전체 주문
   * @param reqeust: 장바구니 id, coupon 사용 여부
   * @param userId: 사용자 loginId
   * @return OrderResponse: 주문 id와 주문상태 message
   * @throws CustomException: 사용자 loginId가 없을 때, 장바구니 id가 존재하지 않을 때
   *                          menu id를 가진 menu가 존재하지 않을 때, 장바구니에 아무것도 존재하지 않을 때
   *                         장바구니에 있는 메뉴 id가 존재하지 않을 때,
   *                         메뉴의 재고 이상 주문하려고 할 때 CustomException 발생
   */
  @PostMapping("/shopping-basket")
  public OrderResponse addOrderFromBasket(@RequestBody OrderAllFromCartInput reqeust, @RequestParam String userId) {
    OrderDto orderDto = orderService.orderAllFromCart(reqeust, userId);
    return OrderResponse.toResponse(orderDto, orderDto.getOrderStatus().getDescription());
  }

  /**
   * 결제 취소 Controller
   * @param orderId: 사용자가 결제 취소할 주문 id
   * @return OrderResponse: 거절한 order id와 message
   */
  @PatchMapping("/cancels/{orderId}")
  public ResponseEntity<OrderResponse> rejectOrderStatus(@PathVariable long orderId, @RequestParam String userId) {
    OrderDto orderDto = orderService.cancelOrder(orderId, userId);
    return ResponseEntity.ok(OrderResponse.toResponse(orderDto, orderDto.getOrderStatus().getDescription()));
  }

}
