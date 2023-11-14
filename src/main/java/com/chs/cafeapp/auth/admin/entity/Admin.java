package com.chs.cafeapp.auth.admin.entity;


import com.chs.cafeapp.auth.type.Authority;
import com.chs.cafeapp.base.BaseEntity;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Admin extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(unique = true)
  private String loginId; // 이메일
  private String password;

  @Enumerated(EnumType.STRING)
  private Authority authority; // admin은 ROLE_ADMIN

  private LocalDateTime lastLoginDateTime;

  public void setLastLoginDateTime(LocalDateTime lastLoginDateTime) {
    this.lastLoginDateTime = lastLoginDateTime;
  }
}
