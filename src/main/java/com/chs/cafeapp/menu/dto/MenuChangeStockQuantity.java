package com.chs.cafeapp.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MenuChangeStockQuantity {
  private long menuId;
  private int quantity;
}
