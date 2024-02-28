package com.chs.cafeapp.domain.cart.dto;

import com.chs.cafeapp.domain.cart.entity.CartMenu;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.Page;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class CartMenuDto {
  private long id;
  private long cartId;
  private long menuId;
  private String menuName;
  private int quantity;

  public static List<CartMenuDto> convertListDtoFromPageEntity(Page<CartMenu> cartMenus) {
    List<CartMenu> cartMenuList = cartMenus.getContent();
    if (cartMenuList == null) {
      return new ArrayList<>();
    }
    return cartMenuList.stream()
        .map(CartMenuDto::of)
        .collect(Collectors.toList());
  }

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
