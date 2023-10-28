package com.chs.cafeapp.cart.dto;

import com.chs.cafeapp.cart.entity.CartMenu;
import java.util.ArrayList;
import java.util.List;
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


  public static List<CartMenuDto> of(List<CartMenu> cartMenuList) {

    if (cartMenuList != null) {
      List<CartMenuDto> cartMenuDtoList = new ArrayList<>();
      for (CartMenu cartMenu : cartMenuList) {
        cartMenuDtoList.add(CartMenuDto.of(cartMenu));
      }
      return cartMenuDtoList;
    }
    return null;
  }
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
