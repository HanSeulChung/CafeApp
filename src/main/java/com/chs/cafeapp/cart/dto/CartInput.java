package com.chs.cafeapp.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CartInput {
  private long menuId;
  private int quantity;
}
