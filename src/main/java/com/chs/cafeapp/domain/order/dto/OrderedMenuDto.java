package com.chs.cafeapp.domain.order.dto;

import com.chs.cafeapp.domain.order.entity.OrderedMenu;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OrderedMenuDto {
  private long id;

  private String userId;

  private long orderId;
  private long menuId;

  private int quantity;
  private int totalPrice;


  public static List<OrderedMenuDto> of(List<OrderedMenu> orderedMenus) {

    if(orderedMenus != null) {
      List<OrderedMenuDto> orderedMenuDtos = new ArrayList<>();
      for (OrderedMenu orderedMenu : orderedMenus) {
        orderedMenuDtos.add(OrderedMenuDto.of(orderedMenu));
      }
      return orderedMenuDtos;
    }
    return new ArrayList<>();
  }

  public static OrderedMenuDto of(OrderedMenu orderedMenu) {
    return OrderedMenuDto.builder()
        .id(orderedMenu.getId())
        .orderId(orderedMenu.getOrder().getId())
        .userId(orderedMenu.getUserId())
        .menuId(orderedMenu.getMenus().getId())
        .quantity(orderedMenu.getQuantity())
        .totalPrice(orderedMenu.getTotalPrice())
        .build();
  }
}
