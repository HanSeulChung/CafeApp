package com.chs.cafeapp.menu.category.dto;

import com.chs.cafeapp.menu.category.entity.Category;
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
public class CategoryDto {
  private long id;

  private String superCategory;
  private String baseCategory;

  public static List<CategoryDto> of(List<Category> categories) {

    if (categories != null) {
      List<CategoryDto> categoryDtoList = new ArrayList<>();
      for(Category x : categories) {
        categoryDtoList.add(of(x));
      }
      return categoryDtoList;
    }

    return null;
  }

  public static CategoryDto of(Category category) {
    return CategoryDto.builder()
        .id(category.getId())
        .superCategory(category.getSuperCategory())
        .baseCategory(category.getBaseCategory())
        .build();
  }

  public static CategoryDto fromInput(CategoryInput categoryInput) {
    return CategoryDto.builder()
        .superCategory(categoryInput.getSuperCategory())
        .baseCategory(categoryInput.getBaseCategory())
        .build();
  }
}
