package com.chs.cafeapp.domain.order.dto;

import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OrderAllFromCartInput {
  @NotNull
  private long cartId;
  @NotNull
  private boolean couponUse;

  private Long couponId;

  @Builder
  public OrderAllFromCartInput(long cartId, boolean couponUse) {
    this.cartId = cartId;
    this.couponUse = couponUse;
  }
}
