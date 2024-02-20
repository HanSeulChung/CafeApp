package com.chs.cafeapp.auth.member.entity;

import com.chs.cafeapp.auth.type.Authority;
import com.chs.cafeapp.auth.member.type.MemberSex;
import com.chs.cafeapp.auth.member.type.MemberStatus;
import com.chs.cafeapp.base.BaseEntity;
import com.chs.cafeapp.cart.entity.Cart;
import com.chs.cafeapp.coupon.entity.Coupon;
import com.chs.cafeapp.stamp.entity.Stamp;
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
public class Member extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String loginId; // 이메일
    private String password;
    private String userName;

    @Column(unique = true)
    private String nickName;
    private int age;
    private String emailAuthKey;

    @Enumerated(EnumType.STRING)
    private MemberSex sex;
    @Enumerated(EnumType.STRING)
    private MemberStatus memberStatus; //이용 가능한상태, 정지상태
    @Enumerated(EnumType.STRING)
    private Authority authority; // 사용자는 ROLE_USER

    private LocalDateTime lastLoginDateTime;

    public void changePassword(String newPassword) {
        this.password = newPassword;
    }
    public void setMemberStatus(MemberStatus memberStatus) {
        this.memberStatus = memberStatus;
    }

    public void setAuthority(Authority authority) {
        this.authority = authority;
    }

    public void setLastLoginDateTime(LocalDateTime lastLoginDateTime) {
        this.lastLoginDateTime = lastLoginDateTime;
    }
    public void setUpdateDateTime() {
        super.setUpdateDateTime();
    }

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
}
