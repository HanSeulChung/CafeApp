package com.chs.cafeapp.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CartMenuChangeQuantity {
  private long cartMenuId;
  private long menuId;
  private int quantity;
}
