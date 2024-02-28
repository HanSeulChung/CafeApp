package com.chs.cafeapp.domain.order.entity;

import com.chs.cafeapp.base.BaseEntity;
import com.chs.cafeapp.domain.order.type.OrderStatus;
import com.chs.cafeapp.auth.member.entity.Member;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="orders")
public class Order extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Enumerated(EnumType.STRING)
  private OrderStatus orderStatus;

  private int totalQuantity;
  private int totalPrice;

  private boolean couponUse;
  private long couponId;

  @ManyToOne(fetch = FetchType.LAZY) // 다대일 단방향 매핑
  @JoinColumn(name = "member_id")
  private Member member;

  @OneToMany(mappedBy = "order") // 일대다 양방향 매핑
  private List<OrderedMenu> orderedMenus;

  public void setMember(Member member) {
    this.member = member;
  }
  public void setOrderedMenus(OrderedMenu orderedMenu) {
    if (this.getOrderedMenus() == null) {
      this.orderedMenus = new ArrayList<>();
    }
    this.orderedMenus.add(orderedMenu);
  }
  public List<OrderedMenu> getOrderedMenus() {
    if (orderedMenus == null) {
      orderedMenus = new ArrayList<>();
    }
    return orderedMenus;
  }

  public void setCouponId(long couponId) {
    this.couponId = couponId;
  }
  public void setOrderStatus(OrderStatus orderStatus) {
    this.orderStatus = orderStatus;
  }
  public void setTotalQuantity(List<OrderedMenu> orderedMenus) {
    if (orderedMenus != null) {
      for (OrderedMenu orderedMenu : orderedMenus) {
        this.totalQuantity += orderedMenu.getQuantity();
      }
    }
  }

  public void setTotalPrice(List<OrderedMenu> orderedMenus) {
    for (OrderedMenu orderedMenu : orderedMenus) {
      this.totalPrice += orderedMenu.getMenus().getPrice() * orderedMenu.getQuantity();
    }
  }

  public void minusTotalPriceByCouponUse(int discountPrice) {
    this.totalPrice -= discountPrice;
  }
}
