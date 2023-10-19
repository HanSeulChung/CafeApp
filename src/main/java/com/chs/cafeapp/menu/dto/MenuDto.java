package com.chs.cafeapp.menu.dto;

import com.chs.cafeapp.menu.entity.Menus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class MenuDto {
  private long id;
  private String menuType;
  private String name;
  private int kcal;
  private String description;
  private int stock;
  private int price;
  private String status;

  public static MenuDto of(Menus menu) {
    return MenuDto.builder()
                .id(menu.getId())
                .menuType(menu.getMenuType())
                .name(menu.getName())
                .kcal(menu.getKcal())
                .description(menu.getDescription())
                .stock(menu.getStock())
                .price(menu.getPrice())
                .status(menu.getStatus())
                .build();
  }

  public static MenuDto fromInput(MenuInput menuInput) {
    return MenuDto.builder()
        .menuType(menuInput.getMenuType())
        .name(menuInput.getName())
        .kcal(menuInput.getKcal())
        .description(menuInput.getDescription())
        .stock(menuInput.getStock())
        .price(menuInput.getPrice())
        .status(menuInput.getStatus())
        .build();
  }

}
