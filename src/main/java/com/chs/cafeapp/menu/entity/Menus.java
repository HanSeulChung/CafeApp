package com.chs.cafeapp.menu.entity;

import com.chs.cafeapp.base.BaseEntity;
import com.chs.cafeapp.menu.dto.MenuDto;
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
public class Menus extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String menuType;
  private String name;
  private int kcal;
  private String description;
  private int stock;
  private int price;
  private String status;

  public static Menus toEntity(MenuDto menuDto) {
    return Menus.builder()
                  .id(menuDto.getId())
                  .menuType(menuDto.getMenuType())
                  .name(menuDto.getName())
                  .kcal(menuDto.getKcal())
                  .description(menuDto.getDescription())
                  .stock(menuDto.getStock())
                  .price(menuDto.getPrice())
                  .status(menuDto.getStatus())
                  .build();
  }

}
