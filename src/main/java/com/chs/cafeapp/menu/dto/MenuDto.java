package com.chs.cafeapp.menu.dto;

import com.chs.cafeapp.coupon.dto.CouponDto;
import com.chs.cafeapp.coupon.entity.Coupon;
import com.chs.cafeapp.menu.entity.Menus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.Page;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class MenuDto {
  private long id;
  private long categoryId;
  private String name;
  private int kcal;
  private String description;
  private int stock;
  private int price;
  private boolean isSoldOut;
  @Setter
  private String menuImageFileUrl;

  private String superCategory;
  private String baseCategory;

  private LocalDateTime createdDateTime;
  private LocalDateTime updatedDateTime;

  public void setSuperCategory(String superCategory) {
    this.superCategory = superCategory;
  }
  public void setBaseCategory(String baseCategory) {
    this.baseCategory = baseCategory;
  }

  public void setSoldOut(boolean soldOut) {
    this.isSoldOut = soldOut;
  }

  public static List<MenuDto> convertListDtoFromPageEntity(Page<Menus> menus) {
    List<Menus> menusList = menus.getContent();

    if (menusList == null) {
      return new ArrayList<>();
    }

    return menusList.stream()
        .map(MenuDto::of)
        .collect(Collectors.toList());
  }
  public static List<MenuDto> of(List<Menus> menusList) {

    if(menusList != null) {
      List<MenuDto> menuDtoList = new ArrayList<>();
      for (Menus menu : menusList) {
        menuDtoList.add(MenuDto.of(menu));
      }
      return menuDtoList;
    }

    return new ArrayList<>();
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
                .menuImageFileUrl(menu.getMenuImageFileUrl())
                .superCategory(menu.getCategory().getSuperCategory())
                .baseCategory(menu.getCategory().getBaseCategory())
                .createdDateTime(menu.getCreateDateTime())
                .updatedDateTime(menu.getUpdateDateTime())
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
