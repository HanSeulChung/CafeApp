package com.chs.cafeapp.user.entity;

import com.chs.cafeapp.base.BaseEntity;
import com.chs.cafeapp.cart.entity.Cart;
import com.chs.cafeapp.stamp.entity.Stamp;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
public class User extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String loginId; // 이메일
    private String password;

    private String userName;
    private String nickName;
    private String sex;
    private int age;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user")
    @ToString.Exclude
    private Cart cart;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user")
    @ToString.Exclude
    private Stamp stamp;

    public void setCart(Cart cart) {
        this.cart = cart;
    }
    public void setStamp(Stamp stamp) {
        this.stamp = stamp;
    }
}
