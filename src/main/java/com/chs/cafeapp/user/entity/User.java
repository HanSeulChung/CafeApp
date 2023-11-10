package com.chs.cafeapp.user.entity;

import com.chs.cafeapp.base.BaseEntity;
import com.chs.cafeapp.cart.entity.Cart;
import com.chs.cafeapp.cart.entity.CartMenu;
import com.chs.cafeapp.coupon.entity.Coupon;
import com.chs.cafeapp.stamp.entity.Stamp;
import com.chs.cafeapp.user.type.UserStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import javax.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String loginId; // 이메일
    private String password;
    private String userName;

    @Column(unique = true)
    private String nickName;
    private String sex;
    private int age;

    @Enumerated(EnumType.STRING)
    private UserStatus userStatus; //이용 가능한상태, 정지상태

    private LocalDateTime lastLoginDateTime;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user")
    @ToString.Exclude
    private Cart cart;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user")
    @ToString.Exclude
    private Stamp stamp;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
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
}
