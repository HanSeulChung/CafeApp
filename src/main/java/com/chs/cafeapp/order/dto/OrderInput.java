package com.chs.cafeapp.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OrderInput {
  private long menuId;
  private String menuName;
  private int menuPrice;
  private int quantity;
  private boolean couponUse;

}
