package com.chs.cafeapp.auth.token.entity;

import com.chs.cafeapp.base.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "refresh_token_key")
  private String key;

  @Column(name = "refresh_token_value")
  private String value;

  public RefreshToken updateValue(String token) {
    this.value = token;
    return this;
  }
}
