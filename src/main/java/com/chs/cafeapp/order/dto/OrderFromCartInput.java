package com.chs.cafeapp.order.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class OrderFromCartInput {
  private long cartId;
  private List<Long> idList;
  private boolean couponUse;
}
