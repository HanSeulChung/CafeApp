package com.chs.cafeapp.domain.menu.category.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CategoryResponse {
  private String superCategory;
  private String baseCategory;

  public static List<CategoryResponse> toResponse(List<CategoryDto> categories) {

    if (categories != null) {
      List<CategoryResponse> categoryResponses = new ArrayList<>();
      for(CategoryDto x : categories) {
        categoryResponses.add(toResponse(x));
      }
      return categoryResponses;
    }
    return null;
  }
  public static CategoryResponse toResponse(CategoryDto categoryDto) {
    return CategoryResponse.builder()
                            .superCategory(categoryDto.getSuperCategory())
                            .baseCategory(categoryDto.getBaseCategory())
                            .build();
  }
}
