package com.chs.cafeapp.cart.entity;

import com.chs.cafeapp.base.BaseEntity;
import com.chs.cafeapp.user.entity.User;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
  @JoinColumn(name = "user_id")
  private User user;

  @OneToMany(mappedBy = "cart", cascade = CascadeType.REMOVE)
  private List<CartMenu> cartMenu = new ArrayList<>();

  public void setUser(User user) {
    this.user = user;
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
      throw new RuntimeException("장바구니에 메뉴가 남아있습니다.");
    }
  }

  public void resetTotalQuantity() {
    if (this.cartMenu.isEmpty()) {
      this.totalQuantity = 0;
    } else {
      throw new RuntimeException("장바구니에 메뉴가 남아있습니다.");
    }
  }
}
