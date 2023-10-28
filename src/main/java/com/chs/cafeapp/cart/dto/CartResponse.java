package com.chs.cafeapp.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CartResponse {
  private long id;
  private long cartId;
  private long menuId;
  private String menuName;
  private int quantity;
  private String message;
  public static CartResponse toResponse(CartMenuDto cartMenuDto, String message) {
    return CartResponse.builder()
                        .id(cartMenuDto.getId())
                        .cartId(cartMenuDto.getCartId())
                        .menuId(cartMenuDto.getMenuId())
                        .menuName(cartMenuDto.getMenuName())
                        .quantity(cartMenuDto.getQuantity())
                        .message(message)
                        .build();
  }
}
