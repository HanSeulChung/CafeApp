package com.chs.cafeapp.domain.stamp.entity;


import com.chs.cafeapp.base.BaseEntity;
import com.chs.cafeapp.auth.member.entity.Member;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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
public class Stamp extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private long stampNumbers;

  @OneToOne
  @JoinColumn(name = "member_id")
  private Member member;

  public void setMember(Member member) {
    this.member = member;
  }

  public void addStamp(long stampCnt) {
    this.stampNumbers += stampCnt;
  }

  public void reCalculateStamp(long newStampCnt) {
    this.stampNumbers = newStampCnt;
  }
}
