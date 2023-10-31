package com.chs.cafeapp.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class CartMenuChangeQuantity {
  private long cartMenuId;
  private long menuId;
  private int quantity;
}
