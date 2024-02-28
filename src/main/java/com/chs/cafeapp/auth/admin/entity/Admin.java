package com.chs.cafeapp.auth.admin.entity;


import com.chs.cafeapp.auth.type.UserStatus;
import com.chs.cafeapp.auth.type.Authority;
import com.chs.cafeapp.base.BaseEntity;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
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
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Admin extends BaseEntity implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(unique = true)
  private String loginId; // 이메일
  @Setter
  private String password;
  private String userName;
  @Setter
  @Enumerated(EnumType.STRING)
  private UserStatus adminStatus; //이용 가능한상태, 정지상태
  @Setter
  @Enumerated(EnumType.STRING)
  private Authority authority; // admin은 ROLE_ADMIN
  @Setter
  private LocalDateTime lastLoginDateTime;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    Collection<GrantedAuthority> authority = new ArrayList<>();
    authority.add(new SimpleGrantedAuthority(this.authority.toString()));
    return authority;
  }
  @Override
  public String getUsername() {
    return this.loginId;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
