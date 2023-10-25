package com.chs.cafeapp.cart.entity;

import com.chs.cafeapp.base.BaseEntity;
import com.chs.cafeapp.user.entity.User;
import java.util.ArrayList;
import java.util.List;
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

  @OneToMany(mappedBy = "cart", fetch = FetchType.EAGER)
  private List<CartMenu> cartMenu = new ArrayList<>();

  public void setUser(User user) {
    this.user = user;
  }

  public void setTotalQuantity(int quantity) {
    this.totalQuantity += quantity;
  }
  public void setTotalPrice(int quantity, int price) {
    this.totalPrice += quantity * price;
  }

  public void minusTotalQuantity(int quantity) {
    this.totalQuantity -= quantity;
  }

  public void minusTotalPrice(int quantity, int price) {
    this.totalPrice -= quantity * price;
  }
}
