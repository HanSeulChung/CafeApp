package com.chs.cafeapp.auth.member.entity;

import com.chs.cafeapp.auth.type.UserSex;
import com.chs.cafeapp.auth.type.UserStatus;
import com.chs.cafeapp.auth.type.Authority;
import com.chs.cafeapp.base.BaseEntity;
import com.chs.cafeapp.domain.cart.entity.Cart;
import com.chs.cafeapp.domain.coupon.entity.Coupon;
import com.chs.cafeapp.domain.stamp.entity.Stamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseEntity implements UserDetails {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String loginId; // 이메일

    @Setter
    private String password;
    private String name;

    @Column(unique = true)
    private String nickName;
    private int age;

    @Enumerated(EnumType.STRING)
    private UserSex sex;

    @Setter
    @Enumerated(EnumType.STRING)
    private UserStatus memberStatus; //이용 가능한상태, 정지상태

    @Setter
    @Enumerated(EnumType.STRING)
    private Authority authority; // 사용자는 ROLE_USER

    @Setter
    private LocalDateTime lastLoginDateTime;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "member")
    @ToString.Exclude
    private Cart cart;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "member")
    @ToString.Exclude
    private Stamp stamp;

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Coupon> coupons;
    public void setCart(Cart cart) {
        this.cart = cart;
    }
    public void setStamp(Stamp stamp) {
        this.stamp = stamp;
    }

    public void setCoupons(Coupon coupon) {
        if (this.getCoupons() == null) {
            this.coupons = new ArrayList<>();
        }
        this.coupons.add(coupon);
    }

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