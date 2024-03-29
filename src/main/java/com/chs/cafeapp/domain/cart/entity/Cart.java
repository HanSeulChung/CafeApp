package com.chs.cafeapp.domain.cart.entity;

import static com.chs.cafeapp.global.exception.type.ErrorCode.REMAIN_CART_MENU_IN_CART;

import com.chs.cafeapp.base.BaseEntity;
import com.chs.cafeapp.global.exception.CustomException;
import com.chs.cafeapp.auth.member.entity.Member;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Cart extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private int totalQuantity;
  private long totalPrice;

  @OneToOne
  @JoinColumn(name = "member_id")
  private Member member;

  @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<CartMenu> cartMenu;

  public void setMember(Member member) {
    this.member = member;
  }

  public void setCartMenu(CartMenu cartMenu) {
    if (this.getCartMenu() == null) {
      this.cartMenu = new ArrayList<>();
    }
    this.cartMenu.add(cartMenu);
  }

  public void addTotalQuantity(int quantity) {
    this.totalQuantity += quantity;
  }
  public void addTotalPrice(int quantity, int price) {
    this.totalPrice += quantity * price;
  }

  public void minusTotalQuantity(int quantity) {
    this.totalQuantity -= quantity;
  }

  public void minusTotalPrice(int quantity, int price) {
    this.totalPrice -= quantity * price;
  }

  public void resetTotalPrice() {
    if (this.cartMenu.isEmpty()) {
      this.totalPrice = 0;
    } else {
      throw new CustomException(REMAIN_CART_MENU_IN_CART);
    }
  }

  public void resetTotalQuantity() {
    if (this.cartMenu.isEmpty()) {
      this.totalQuantity = 0;
    } else {
      throw new CustomException(REMAIN_CART_MENU_IN_CART);
    }
  }
}
