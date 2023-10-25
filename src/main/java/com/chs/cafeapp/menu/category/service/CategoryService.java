package com.chs.cafeapp.menu.category.service;

import com.chs.cafeapp.menu.category.dto.CategoryDto;
import com.chs.cafeapp.menu.category.dto.CategoryEditInput;
import com.chs.cafeapp.menu.category.dto.CategoryInput;
import java.util.List;

public interface CategoryService {
  /**
   * 카테고리 추가
   */
  CategoryDto add(CategoryInput categoryInput);

  /**
   * 카테고리 수정
   */
  CategoryDto edit(CategoryEditInput categoryEditInput);

  /**
   * 카테고리 삭제
   */
  void delete(Long categoryId);

  /**
   * 카테고리 전체 조회
   */
  List<CategoryDto> readAll();

  /**
   * 카테고리 대분류로 조회
   */
  List<CategoryDto> readSuperCategory(String superCategory);
}
