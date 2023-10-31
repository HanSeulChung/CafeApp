package com.chs.cafeapp.menu.category.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CategoryEditInput {
  private long id;
  private String superCategory;
  private String baseCategory;
}
