package com.chs.cafeapp.cart.dto;

import com.chs.cafeapp.cart.entity.CartMenu;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CartMenuDto {
  private long id;
  private long cartId;
  private long menuId;
  private String menuName;
  private int quantity;

  public static CartMenuDto of(CartMenu cartMenu) {
    return CartMenuDto.builder()
                      .id(cartMenu.getId())
                      .cartId(cartMenu.getCart().getId())
                      .menuId(cartMenu.getMenus().getId())
                      .menuName(cartMenu.getMenus().getName())
                      .quantity(cartMenu.getQuantity())
                      .build();
  }
}
