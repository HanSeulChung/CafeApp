package com.chs.cafeapp.order.dto;

import com.chs.cafeapp.order.entity.OrderedMenu;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
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
    return null;
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
