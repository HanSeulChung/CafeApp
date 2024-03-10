package com.chs.cafeapp.domain.menu.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuResponse {

  private String name;
  private int kcal;
  private String description;
  private int stock;
  private int price;
  private boolean isSoldOut;
  private String menuImageFileUrl;

  private String superCategory;
  private String baseCategory;

  public static List<MenuResponse> toResponse(List<MenuDto> menuDtos) {

    if (menuDtos != null) {
      List<MenuResponse> menuResponses = new ArrayList<>();
      for(MenuDto menuDto : menuDtos) {
        menuResponses.add(toResponse(menuDto));
      }
      return menuResponses;
    }
    return new ArrayList<>();
  }
  public static MenuResponse toResponse(MenuDto menuDto) {
    return MenuResponse.builder()
                        .name(menuDto.getName())
                        .kcal(menuDto.getKcal())
                        .description(menuDto.getDescription())
                        .stock(menuDto.getStock())
                        .price(menuDto.getPrice())
                        .isSoldOut(menuDto.isSoldOut())
                        .menuImageFileUrl(menuDto.getMenuImageFileUrl())
                        .superCategory(menuDto.getSuperCategory())
                        .baseCategory(menuDto.getBaseCategory())
                        .build();
  }
}
