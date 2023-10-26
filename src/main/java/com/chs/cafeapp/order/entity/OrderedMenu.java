package com.chs.cafeapp.order.entity;

import com.chs.cafeapp.base.BaseEntity;
import com.chs.cafeapp.cart.entity.CartMenu;
import com.chs.cafeapp.menu.entity.Menus;
import com.chs.cafeapp.order.dto.OrderedMenuDto;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderedMenu extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String userId;
  private int quantity;
  private int totalPrice;

  @ManyToOne(fetch = FetchType.LAZY) // 다대일 양방향 매핑
  @JoinColumn(name = "order_id")
  private Order order;

  @ManyToOne(fetch = FetchType.LAZY) // 다대일 단방향 매핑
  @JoinColumn(name = "menus_id")
  private Menus menus;


  public void setOrder(Order order) {
    this.order = order;
    order.getOrderedMenus().add(this);
  }

  public void setMenus(Menus menus) {
    this.menus = menus;
  }
  public static List<OrderedMenu> fromDto(List<OrderedMenuDto> orderedMenuDtos) {
    List<OrderedMenu> orderedMenus = new ArrayList<>();

    if(orderedMenuDtos != null) {
      for (OrderedMenuDto orderedMenuDto : orderedMenuDtos) {
        orderedMenus.add(OrderedMenu.fromDto(orderedMenuDto));
      }
    }
    return orderedMenus;
  }

  public static OrderedMenu fromDto(OrderedMenuDto orderedMenuDto) {
    return OrderedMenu.builder()
                      .id(orderedMenuDto.getId())
                      .userId(orderedMenuDto.getUserId())
                      .quantity(orderedMenuDto.getQuantity())
                      .totalPrice(orderedMenuDto.getTotalPrice())
                      .build();
  }

  public static List<OrderedMenu> fromCartMenu(List<CartMenu> cartMenuList) {
    List<OrderedMenu> orderedMenuList = new ArrayList<>();
    if (cartMenuList != null) {
      for (CartMenu x : cartMenuList) {
        orderedMenuList.add(OrderedMenu.fromCartMenu(x));
      }
    }
    return orderedMenuList;
  }

  public static OrderedMenu fromCartMenu(CartMenu cartMenu) {
    return OrderedMenu.builder()
        .userId(cartMenu.getCart().getUser().getLoginId())
        .quantity(cartMenu.getQuantity())
        .totalPrice(cartMenu.getQuantity() * cartMenu.getMenus().getPrice())
        .menus(cartMenu.getMenus())
        .build();
  }
}
