package com.chs.cafeapp.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class MenuResponse {
  private String menuType;
  private String name;
  private int kcal;
  private String description;
  private int stock;
  private int price;
  private String status;

  public static MenuResponse toResponse(MenuDto menuDto) {
    return MenuResponse.builder()
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
