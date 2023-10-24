package com.chs.cafeapp.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
public class OrderInput {
  private long menuId;
  private String menuName;
  private int menuPrice;
  private int quantity;
  private boolean couponUse;

//  public OrderInput(OrderInput originInput, String userLoginId) {
//    this.userLoginId = userLoginId;
//    this.menuId = originInput.getMenuId();
//    this.menuName = originInput.getMenuName();
//    this.menuPrice = originInput.getMenuPrice();
//    this.count = originInput.getCount();
//  }
}
