package com.chs.cafeapp.domain.menu.category.entity;

import com.chs.cafeapp.base.BaseEntity;
import com.chs.cafeapp.domain.menu.category.dto.CategoryDto;
import javax.persistence.Entity;
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
public class Category extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  // 대, 중분류 카테고리 <소분류는 추후 필요하면 추가>
  private String superCategory;
  private String baseCategory;

  public static Category toEntity(CategoryDto categoryDto) {
    return Category.builder()
                    .superCategory(categoryDto.getSuperCategory())
                    .baseCategory(categoryDto.getBaseCategory())
                    .build();
  }
}
