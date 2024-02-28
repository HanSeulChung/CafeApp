package com.chs.cafeapp.domain.menu.dto;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MenuChangeStockQuantity {
  @NotNull
  private long menuId;
  @NotNull
  private int quantity;
}
