package com.chs.cafeapp.domain.cart.dto;

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
