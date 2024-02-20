package com.chs.cafeapp.coupon.entity;


import com.chs.cafeapp.base.BaseEntity;
import com.chs.cafeapp.auth.member.entity.Member;
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
  @JoinColumn(name = "member_id")
  private Member member;

  private String couponName;
  private int price;
  private boolean usedYn;
  private LocalDateTime expirationDateTime;
  private boolean expiredYn;
  public void setMember(Member member) {
    this.member = member;
  }

  public void setUsedYn(boolean usedYn) {
    this.usedYn = usedYn;
  }

  public void setExpiredYn(boolean expiredYn) {
    this.expiredYn = expiredYn;
  }
}
