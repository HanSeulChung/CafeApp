package com.chs.cafeapp.domain.cart.entity;

import com.chs.cafeapp.base.BaseEntity;
import com.chs.cafeapp.domain.menu.entity.Menus;
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
public class CartMenu extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private int quantity;

  @ManyToOne(fetch = FetchType.LAZY) // 다대일 양방향 매핑
  @JoinColumn(name = "cart_id")
  private Cart cart;

  @ManyToOne(fetch = FetchType.LAZY) // 다대일 단방향 매핑
  @JoinColumn(name = "menus_id")
  private Menus menus;

  public void setMenus(Menus menus) {
    this.menus = menus;
  }
  public void setQuantity(int quantity){
    this.quantity = quantity;
  }

  public void addQuantity(int quantity) {
    this.quantity += quantity;
  }

  public void minusQuantity(int quantity) {
    this.quantity -= quantity;
  }
  public void setCart(Cart cart) {
    this.cart = cart;
  }
}
