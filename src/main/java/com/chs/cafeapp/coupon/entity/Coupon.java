package com.chs.cafeapp.coupon.entity;


import com.chs.cafeapp.base.BaseEntity;
import com.chs.cafeapp.user.entity.User;
import java.time.LocalDateTime;
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
public class Coupon extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @ManyToOne(fetch = FetchType.LAZY) // 다대일 양방향 매핑
  @JoinColumn(name = "user_id")
  private User user;

  private String couponName;
  private int price;
  private boolean usedYn;
  private LocalDateTime expirationDateTime;
  private boolean expiredYn;
  private void setUser(User user) {
    this.user = user;
  }
}
