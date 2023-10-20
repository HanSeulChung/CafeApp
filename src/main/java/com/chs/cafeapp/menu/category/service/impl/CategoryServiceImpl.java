package com.chs.cafeapp.menu.category.service.impl;

import com.chs.cafeapp.menu.category.dto.CategoryDto;
import com.chs.cafeapp.menu.category.dto.CategoryEditInput;
import com.chs.cafeapp.menu.category.dto.CategoryInput;
import com.chs.cafeapp.menu.category.entity.Category;
import com.chs.cafeapp.menu.category.repository.CategoryRepository;
import com.chs.cafeapp.menu.category.service.CategoryService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@AllArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {
  private final CategoryRepository categoryRepository;
  @Override
  public CategoryDto add(CategoryInput categoryInput) {
    boolean existsCategory = categoryRepository.existsBySuperCategoryAndBaseCategory(
        categoryInput.getSuperCategory(),
        categoryInput.getBaseCategory());

    if (existsCategory) {
      throw new RuntimeException("이미 존재하는 카테고리 이름입니다.");
    }

    CategoryDto categoryDto = CategoryDto.fromInput(categoryInput);
    Category category = Category.toEntity(categoryDto);
    categoryRepository.save(category);

    return categoryDto;
  }

  @Override
  public CategoryDto edit(CategoryEditInput categoryEditInput) {
    Category category = categoryRepository.findById(categoryEditInput.getId())
        .orElseThrow(() -> new RuntimeException("카테고리가 존재하지 않습니다."));

    boolean existsCategory = categoryRepository.existsBySuperCategoryAndBaseCategory(
                                                categoryEditInput.getSuperCategory(),
                                                categoryEditInput.getBaseCategory());

    if (existsCategory) {
      throw new RuntimeException("이미 존재하는 카테고리 이름입니다.");
    }

    return CategoryDto.of(categoryRepository.save(
                            Category.builder()
                                .id(category.getId())
                                .superCategory(categoryEditInput.getSuperCategory())
                                .baseCategory(categoryEditInput.getBaseCategory())
                                .build()));
  }

  @Override
  public void delete(Long categoryId) {
    Category category = categoryRepository.findById(categoryId)
        .orElseThrow(() -> new RuntimeException("존재하지 않는 카테고리입니다."));

    categoryRepository.deleteById(categoryId);
  }

  @Override
  public List<CategoryDto> readAll() {
    return CategoryDto.of(categoryRepository.findAll());
  }

  @Override
  public List<CategoryDto> readSuperCategory(String superCategory) {
    return CategoryDto.of(categoryRepository.findAllBySuperCategory(superCategory));
  }
}
