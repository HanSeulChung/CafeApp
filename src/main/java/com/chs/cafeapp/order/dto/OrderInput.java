package com.chs.cafeapp.order.dto;

import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OrderInput {
  @NotNull
  private long menuId;
  @NotNull
  private String menuName;
  @NotNull
  private int menuPrice;
  @NotNull
  private int quantity;
  @NotNull
  private boolean couponUse;

  private Long couponId;

  @Builder
  public OrderInput(long menuId, String menuName, int menuPrice, int quantity, boolean couponUse) {
    this.menuId = menuId;
    this.menuName = menuName;
    this.menuPrice = menuPrice;
    this.quantity = quantity;
    this.couponUse = couponUse;
  }

  public void setCouponId(Long couponId) {
    this.couponId = couponId;
  }
}
