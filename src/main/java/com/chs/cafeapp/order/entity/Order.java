package com.chs.cafeapp.order.entity;

import com.chs.cafeapp.base.BaseEntity;
import com.chs.cafeapp.order.dto.OrderDto;
import com.chs.cafeapp.order.dto.OrderInput;
import com.chs.cafeapp.order.type.OrderStatus;
import com.chs.cafeapp.user.entity.User;
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

  private int totalQuantity = 0;
  private int totalPrice = 0;

  private boolean couponUse;

  @ManyToOne(fetch = FetchType.LAZY) // 다대일 단방향 매핑
  @JoinColumn(name = "user_id")
  private User user;

  @OneToMany(mappedBy = "order") // 일대다 양방향 매핑
  private List<OrderedMenu> orderedMenus = new ArrayList<>();

  public void setUser(User user) {
    this.user = user;
  }

  public List<OrderedMenu> getOrderedMenus() {
    if (orderedMenus == null) {
      orderedMenus = new ArrayList<>();
    }
    return orderedMenus;
  }
  public void setOrderStatus(OrderStatus orderStatus) {
    this.orderStatus = orderStatus;
  }
  public void setTotalQuantity(List<OrderedMenu> orderedMenus) {

    for (OrderedMenu orderedMenu : orderedMenus) {
      this.totalQuantity += orderedMenu.getQuantity();
    }
  }

  public void setTotalPrice(List<OrderedMenu> orderedMenus) {
    for (OrderedMenu orderedMenu : orderedMenus) {
      this.totalPrice += orderedMenu.getMenus().getPrice() * orderedMenu.getQuantity();
    }
  }
  public static Order fromOrderInput(OrderInput orderInput) {
    return Order.builder()
                .totalQuantity(orderInput.getQuantity())
                .totalPrice(orderInput.getMenuPrice() * orderInput.getQuantity())
                .couponUse(orderInput.isCouponUse())
                .build();
  }

  public static Order toEntity(OrderDto orderDto) {
    return Order.builder()
                .id(orderDto.getId())
                .orderStatus(orderDto.getOrderStatus())
                .couponUse(orderDto.isCouponUse())
                .totalQuantity(orderDto.getTotalQuantity())
                .totalPrice(orderDto.getTotalPrice())
                .orderedMenus(OrderedMenu.fromDto(orderDto.getOrderedMenus()))
                .build();
  }
}
