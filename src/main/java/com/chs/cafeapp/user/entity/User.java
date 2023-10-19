package com.chs.cafeapp.user.entity;

import com.chs.cafeapp.base.BaseEntity;
import com.chs.cafeapp.grade.entity.Grade;
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

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String userId; // 이메일
    private String password;
    
    private String userName;
    private String nickName;
    private String sex;
    private int age;

    @OneToOne(mappedBy = "user")
    private Grade grade;

    public void setGrade(Grade grade) {
        this.grade = grade;
    }
}
