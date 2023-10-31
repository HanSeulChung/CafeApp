package com.chs.cafeapp.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MenuInput {
  private String name;
  private int kcal;
  private String description;
  private int stock;
  private int price;
  private String status;

  private String superCategory;
  private String baseCategory;
}
