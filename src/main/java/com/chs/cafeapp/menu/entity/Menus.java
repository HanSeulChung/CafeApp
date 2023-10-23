package com.chs.cafeapp.menu.entity;

import com.chs.cafeapp.base.BaseEntity;
import com.chs.cafeapp.menu.category.entity.Category;
import com.chs.cafeapp.menu.dto.MenuDto;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Menus extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String name;
  private int kcal;
  private String description;
  private int stock;
  private int price;
  private boolean isSoldOut;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="category_id")
  private Category category;

  public void setCategory(Category category) {
    this.category = category;
  }
  public static Menus toEntity(MenuDto menuDto) {
    return Menus.builder()
                  .id(menuDto.getId())
                  .name(menuDto.getName())
                  .kcal(menuDto.getKcal())
                  .description(menuDto.getDescription())
                  .stock(menuDto.getStock())
                  .price(menuDto.getPrice())
                  .isSoldOut(menuDto.isSoldOut())
                  .build();
  }
}
