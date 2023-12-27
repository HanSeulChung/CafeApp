package com.chs.cafeapp.menu.dto;

import com.chs.cafeapp.menu.entity.Menus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MenuDetail {
  private long id;
  private String name;
  private int kcal;
  private String description;
  private int stock;
  private int price;
  private boolean isSoldOut;
  private String menuImageFileUrl;

  private String superCategory;
  private String baseCategory;

  private LocalDateTime createdDateTime;
  private LocalDateTime updatedDateTime;

  public static MenuDetail toDetail(MenuDto menuDto) {
    return MenuDetail.builder()
        .id(menuDto.getId())
        .name(menuDto.getName())
        .kcal(menuDto.getKcal())
        .description(menuDto.getDescription())
        .stock(menuDto.getStock())
        .price(menuDto.getPrice())
        .isSoldOut(menuDto.isSoldOut())
        .menuImageFileUrl(menuDto.getMenuImageFileUrl())
        .superCategory(menuDto.getSuperCategory())
        .baseCategory(menuDto.getBaseCategory())
        .createdDateTime(menuDto.getCreatedDateTime())
        .updatedDateTime(menuDto.getUpdatedDateTime())
        .build();
  }
}
