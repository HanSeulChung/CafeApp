package com.chs.cafeapp.menu.category.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CategoryEditInput {
  private long id;
  private String superCategory;
  private String baseCategory;
}
