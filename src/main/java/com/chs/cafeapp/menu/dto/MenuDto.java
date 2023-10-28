package com.chs.cafeapp.menu.dto;

import com.chs.cafeapp.menu.entity.Menus;
import java.util.ArrayList;
import java.util.List;
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
  private long categoryId;
  private String name;
  private int kcal;
  private String description;
  private int stock;
  private int price;
  private boolean isSoldOut;

  private String superCategory;
  private String baseCategory;

  public static List<MenuDto> of(List<Menus> menusList) {

    if(menusList != null) {
      List<MenuDto> menuDtoList = new ArrayList<>();
      for (Menus menu : menusList) {
        menuDtoList.add(MenuDto.of(menu));
      }
      return menuDtoList;
    }

    return null;
  }

  public static MenuDto of(Menus menu) {
    return MenuDto.builder()
                .id(menu.getId())
                .categoryId(menu.getCategory().getId())
                .name(menu.getName())
                .kcal(menu.getKcal())
                .description(menu.getDescription())
                .stock(menu.getStock())
                .price(menu.getPrice())
                .isSoldOut(menu.isSoldOut())
                .superCategory(menu.getCategory().getSuperCategory())
                .baseCategory(menu.getCategory().getBaseCategory())
                .build();
  }

  public static MenuDto fromInput(MenuInput menuInput) {
    return MenuDto.builder()
        .name(menuInput.getName())
        .kcal(menuInput.getKcal())
        .description(menuInput.getDescription())
        .stock(menuInput.getStock())
        .price(menuInput.getPrice())
        .isSoldOut(false)
        .superCategory(menuInput.getSuperCategory())
        .baseCategory(menuInput.getBaseCategory())
        .build();
  }

}
