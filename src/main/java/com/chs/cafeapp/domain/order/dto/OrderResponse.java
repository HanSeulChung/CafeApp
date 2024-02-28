package com.chs.cafeapp.domain.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OrderResponse {
  private long id;
  private String message;
  public static OrderResponse toResponse(OrderDto orderDto, String message) {
    return OrderResponse.builder()
                  .id(orderDto.getId())
                  .message(message)
                  .build();
  }
}
