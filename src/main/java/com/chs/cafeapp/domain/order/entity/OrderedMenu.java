package com.chs.cafeapp.domain.order.entity;

import com.chs.cafeapp.base.BaseEntity;
import com.chs.cafeapp.domain.cart.entity.CartMenu;
import com.chs.cafeapp.domain.menu.entity.Menus;
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
  }

  public void setMenus(Menus menus) {
    this.menus = menus;
  }

  public static List<OrderedMenu> fromCartMenu(List<CartMenu> cartMenuList, Order order) {
    List<OrderedMenu> orderedMenuList = new ArrayList<>();
    if (cartMenuList != null) {
      for (CartMenu x : cartMenuList) {
        orderedMenuList.add(OrderedMenu.fromCartMenu(x , order));
      }
    }
    return orderedMenuList;
  }

  public static List<OrderedMenu> fromCartMenu(List<CartMenu> cartMenuList) {
    if (cartMenuList != null) {
      List<OrderedMenu> orderedMenuList = new ArrayList<>();
      for (CartMenu cartMenu : cartMenuList) {
        orderedMenuList.add(OrderedMenu.fromCartMenu(cartMenu));
      }
      return orderedMenuList;
    }
    return null;
  }

  public static OrderedMenu fromCartMenu(CartMenu cartMenu) {
    return OrderedMenu.builder()
        .userId(cartMenu.getCart().getMember().getLoginId())
        .quantity(cartMenu.getQuantity())
        .totalPrice(cartMenu.getQuantity() * cartMenu.getMenus().getPrice())
        .menus(cartMenu.getMenus())
        .build();
  }
  public static OrderedMenu fromCartMenu(CartMenu cartMenu, Order order) {
    return OrderedMenu.builder()
        .userId(cartMenu.getCart().getMember().getLoginId())
        .quantity(cartMenu.getQuantity())
        .totalPrice(cartMenu.getQuantity() * cartMenu.getMenus().getPrice())
        .menus(cartMenu.getMenus())
        .order(order)
        .build();
  }
}
