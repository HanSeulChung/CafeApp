package com.chs.cafeapp.order.controller;


import com.chs.cafeapp.exception.CustomException;
import com.chs.cafeapp.order.dto.OrderAllFromCartInput;
import com.chs.cafeapp.order.dto.OrderDto;
import com.chs.cafeapp.order.dto.OrderFromCartInput;
import com.chs.cafeapp.order.dto.OrderInput;
import com.chs.cafeapp.order.dto.OrderResponse;
import com.chs.cafeapp.order.service.OrderServiceForAdmin;
import com.chs.cafeapp.order.service.OrderServiceForUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
@RequestMapping("/orders")
public class OrderController {
  private final OrderServiceForUser orderServiceForUser;


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
    OrderDto orderDto = orderServiceForUser.orderIndividualMenu(request, userId);
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
    OrderDto orderDto = orderServiceForUser.orderFromCart(request, userId);
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
    OrderDto orderDto = orderServiceForUser.orderAllFromCart(reqeust, userId);
    return OrderResponse.toResponse(orderDto, orderDto.getOrderStatus().getDescription());
  }

  /**
   * 결제 취소 Controller
   * @param orderId: 사용자가 결제 취소할 주문 id
   * @return OrderResponse: 거절한 order id와 message
   */
  @PatchMapping("/cancels/{orderId}")
  public ResponseEntity<OrderResponse> rejectOrderStatus(@PathVariable long orderId, @RequestParam String userId) {
    OrderDto orderDto = orderServiceForUser.cancelOrder(orderId, userId);
    return ResponseEntity.ok(OrderResponse.toResponse(orderDto, orderDto.getOrderStatus().getDescription()));
  }

  /**
   * 전체 주문 조회 Controller
   * @param userId: 주문한 사용자 loginId
   * @param pageable: default -> page = 0, size = 10이고 수정 가능,
   *                + 정렬 기능 >sortBy=정렬할 기준 column-asc
   * @return Slice<OrderDto>: 전체 주문 Slice로 반환
   */
  @GetMapping()
  public ResponseEntity<Slice<OrderDto>> viewOrdersAll(@RequestParam String userId, Pageable pageable) {
    return ResponseEntity.ok(orderServiceForUser.viewAllOrders(userId, pageable));
  }

  /**
   * 하루 전체 주문 조회 Controller
   * @param userId: 주문한 사용자 loginId
   * @param pageable: default -> page = 0, size = 10이고 수정 가능,
   *                + 정렬 기능 >sortBy=정렬할 기준 column-asc
   * @return Slice<OrderDto>: 하루 전체 주문 Slice로 반환
   */
  @GetMapping("/days")
  public ResponseEntity<Slice<OrderDto>> viewOrdersDuringDays(
      @RequestParam String userId,
      Pageable pageable
  ) {
    return ResponseEntity.ok(orderServiceForUser.viewAllOrdersDuringDays(userId, pageable));
  }

  /**
   * 일주일 전체 주문 조회 Controller
   * @param userId: 주문한 사용자 loginId
   * @param pageable: default -> page = 0, size = 10이고 수정 가능,
   *                + 정렬 기능 >sortBy=정렬할 기준 column-asc
   * @return Slice<OrderDto>: 일주일 전체 주문 Slice로 반환
   */
  @GetMapping("/weeks")
  public ResponseEntity<Slice<OrderDto>> viewOrdersDuringWeeks(
      @RequestParam String userId,
      Pageable pageable
  ) {
    return ResponseEntity.ok(orderServiceForUser.viewAllOrdersDuringWeeks(userId, pageable));
  }

  /**
   * 한달 전체 주문 조회 Controller
   * @param userId: 주문한 사용자 loginId
   * @param pageable: default -> page = 0, size = 10이고 수정 가능,
   *                + 정렬 기능 >sortBy=정렬할 기준 column-asc
   * @return Slice<OrderDto>: 한달 전체 주문 Slice로 반환
   */
  @GetMapping("/months")
  public ResponseEntity<Slice<OrderDto>> viewOrdersDuringMonths(
      @RequestParam String userId,
      Pageable pageable
  ) {
    return ResponseEntity.ok(orderServiceForUser.viewAllOrdersDuringMonths(userId, pageable));
  }

  /**
   * 3개월 전체 주문 조회 Controller
   * @param userId: 주문한 사용자 loginId
   * @param pageable: default -> page = 0, size = 10이고 수정 가능,
   *                + 정렬 기능 >sortBy=정렬할 기준 column-asc
   * @return Slice<OrderDto>: 3개월 전체 주문 Slice로 반환
   */
  @GetMapping("/three-months")
  public ResponseEntity<Slice<OrderDto>> viewOrdersDuringThreeMonths(
      @RequestParam String userId,
      Pageable pageable
  ) {
    return ResponseEntity.ok(orderServiceForUser.viewAllOrdersDuringThreeMonths(userId, pageable));
  }

  /**
   * 6개월 전체 주문 조회 Controller
   * @param userId: 주문한 사용자 loginId
   * @param pageable: default -> page = 0, size = 10이고 수정 가능,
   *                + 정렬 기능 >sortBy=정렬할 기준 column-asc
   * @return Slice<OrderDto>: 6개월 전체 주문 Slice로 반환
   */
  @GetMapping("/six-months")
  public ResponseEntity<Slice<OrderDto>> viewOrdersDuringSixMonths(
      @RequestParam String userId,
      Pageable pageable
  ) {
    return ResponseEntity.ok(orderServiceForUser.viewAllOrdersDuringSixMonths(userId, pageable));
  }

  /**
   * 1년 전체 주문 조회 Controller
   * @param userId: 주문한 사용자 loginId
   * @param pageable: default -> page = 0, size = 10이고 수정 가능,
   *                + 정렬 기능 >sortBy=정렬할 기준 column-asc
   * @return Slice<OrderDto>: 1년 전체 주문 Slice로 반환
   */
  @GetMapping("/years")
  public ResponseEntity<Slice<OrderDto>> viewOrdersDuringYears(
      @RequestParam String userId,
      Pageable pageable
  ) {
    return ResponseEntity.ok(orderServiceForUser.viewAllOrdersDuringYears(userId, pageable));
  }
}
