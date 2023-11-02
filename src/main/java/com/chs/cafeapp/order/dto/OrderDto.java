package com.chs.cafeapp.order.dto;

import com.chs.cafeapp.order.entity.Order;
import com.chs.cafeapp.order.type.OrderStatus;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;


@Getter
@Builder
@ToString
@AllArgsConstructor
public class OrderDto {
  private long id;

  private OrderStatus orderStatus;

  private int totalQuantity;
  private int totalPrice;

  private boolean couponUse;

  private String userId;

  private List<OrderedMenuDto> orderedMenus;

  public static List<OrderDto> of(List<Order> orderList) {

    if (orderList != null) {
      List<OrderDto> orderDtoList = new ArrayList<>();
      for (Order order : orderList) {
        orderDtoList.add(OrderDto.of(order));
      }
      return orderDtoList;
    }

    return null;
  }
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
