package com.chs.cafeapp.cart.dto;

import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class CartMenuQuantityMinus {
  private long id;
  private long menuId;
  private int quantity;
}
