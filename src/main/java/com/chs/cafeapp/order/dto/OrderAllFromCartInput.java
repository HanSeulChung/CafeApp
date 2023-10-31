package com.chs.cafeapp.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OrderAllFromCartInput {
  private long cartId;
  private boolean couponUse;
}
