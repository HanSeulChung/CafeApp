package com.chs.cafeapp.grade.entity;

import com.chs.cafeapp.base.BaseEntity;
import com.chs.cafeapp.user.entity.User;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
public class Grade extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private String grade_name;
  private int amount;  // 주문 양
  private int prices; //총 금액

  @OneToOne
  private User user;
}
