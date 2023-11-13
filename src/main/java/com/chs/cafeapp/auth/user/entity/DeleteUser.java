package com.chs.cafeapp.auth.user.entity;


import com.chs.cafeapp.base.BaseEntity;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteUser extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(unique = true)
  private String loginId; // 이메일
  private String userName;

  private LocalDateTime deleteDateTime;

}
