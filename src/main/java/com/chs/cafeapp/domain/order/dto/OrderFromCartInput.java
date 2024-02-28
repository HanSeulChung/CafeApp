package com.chs.cafeapp.domain.order.dto;

import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OrderFromCartInput {
  @NotNull
  private long cartId;
  @NotNull
  private List<Long> idList;
  @NotNull
  private boolean couponUse;

  private Long couponId;

  @Builder
  public OrderFromCartInput(long cartId, List<Long> idList, boolean couponUse) {
    this.cartId = cartId;
    this.idList = idList;
    this.couponUse = couponUse;
  }
}
