package com.chs.cafeapp.order.dto;

import com.chs.cafeapp.coupon.dto.CouponDto;
import com.chs.cafeapp.coupon.entity.Coupon;
import com.chs.cafeapp.order.entity.Order;
import com.chs.cafeapp.order.type.OrderStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;


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

  public static List<OrderDto> convertListDtoFromPageEntity(Slice<Order> orders) {
    List<Order> orderList = orders.getContent();

    if (orderList == null) {
      return new ArrayList<>();
    }

    return orderList.stream()
        .map(OrderDto::of)
        .collect(Collectors.toList());
  }

  public static List<OrderDto> of(List<Order> orderList) {

    if (orderList != null) {
      List<OrderDto> orderDtoList = new ArrayList<>();
      for (Order order : orderList) {
        orderDtoList.add(OrderDto.of(order));
      }
      return orderDtoList;
    }

    return new ArrayList<>();
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
