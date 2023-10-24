package com.chs.cafeapp.order.dto;

import com.chs.cafeapp.order.entity.Order;
import com.chs.cafeapp.order.type.OrderStatus;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@AllArgsConstructor
@Builder
@Data
public class OrderDto {
  private long id;

  private OrderStatus orderStatus;

  private int totalQuantity = 0;
  private int totalPrice = 0;

  private boolean couponUse;

  private String userId;

  private List<OrderedMenuDto> orderedMenus;

  public static OrderDto of(Order order) {
    return OrderDto.builder()
        .id(order.getId())
        .orderStatus(order.getOrderStatus())
        .totalPrice(order.getTotalPrice())
        .totalQuantity(order.getTotalQuantity())
        .couponUse(order.isCouponUse())
        .userId(order.getUser().getLoginId())
        .orderedMenus(OrderedMenuDto.of(order.getOrderedMenus()))
        .build();
  }
}
