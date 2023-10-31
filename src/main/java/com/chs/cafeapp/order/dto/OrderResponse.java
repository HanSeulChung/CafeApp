package com.chs.cafeapp.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OrderResponse {
  private long id;
  private String message;
  public static OrderResponse toResponse(OrderDto orderDto) {
    return OrderResponse.builder()
                  .id(orderDto.getId())
                  .message("주문이 완료되었습니다. 카페에서 상품 확인 중입니다.")
                  .build();
  }
}
