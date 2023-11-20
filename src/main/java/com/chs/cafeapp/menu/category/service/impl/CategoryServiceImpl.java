package com.chs.cafeapp.menu.category.service.impl;

import static com.chs.cafeapp.exception.type.ErrorCode.CATEGORY_NOT_FOUND;
import static com.chs.cafeapp.exception.type.ErrorCode.EXIST_CATEGORY_NAME;
import static com.chs.cafeapp.exception.type.ErrorCode.EXIST_MENU_BY_CATEGORY;

import com.chs.cafeapp.exception.CustomException;
import com.chs.cafeapp.menu.category.dto.CategoryDto;
import com.chs.cafeapp.menu.category.dto.CategoryEditInput;
import com.chs.cafeapp.menu.category.dto.CategoryInput;
import com.chs.cafeapp.menu.category.entity.Category;
import com.chs.cafeapp.menu.category.repository.CategoryRepository;
import com.chs.cafeapp.menu.category.service.CategoryService;
import com.chs.cafeapp.menu.entity.Menus;
import com.chs.cafeapp.menu.repository.MenuRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {
  private final MenuRepository menuRepository;
  private final CategoryRepository categoryRepository;
  @Override
  public CategoryDto add(CategoryInput categoryInput) {
    boolean existsCategory = categoryRepository.existsBySuperCategoryAndBaseCategory(
        categoryInput.getSuperCategory(),
        categoryInput.getBaseCategory());

    if (existsCategory) {
      throw new CustomException(EXIST_CATEGORY_NAME);
    }

    CategoryDto categoryDto = CategoryDto.fromInput(categoryInput);
    Category category = Category.toEntity(categoryDto);
    categoryRepository.save(category);

    return categoryDto;
  }
  @Override
  public CategoryDto edit(CategoryEditInput categoryEditInput) {
    Category category = categoryRepository.findById(categoryEditInput.getId())
        .orElseThrow(() -> new CustomException(CATEGORY_NOT_FOUND));

    boolean existsCategory = categoryRepository.existsBySuperCategoryAndBaseCategory(
                                                categoryEditInput.getSuperCategory(),
                                                categoryEditInput.getBaseCategory());

    if (existsCategory) {
      throw new CustomException(EXIST_CATEGORY_NAME);
    }

    return CategoryDto.of(categoryRepository.save(
                            Category.builder()
                                .id(category.getId())
                                .superCategory(categoryEditInput.getSuperCategory())
                                .baseCategory(categoryEditInput.getBaseCategory())
                                .build()));
  }
  private void validationCategory(Long categoryId) {
    Category category = categoryRepository.findById(categoryId)
        .orElseThrow(() -> new CustomException(CATEGORY_NOT_FOUND));
    List<Menus> allMenusByCategoryId = menuRepository.findAllByCategoryId(categoryId);

    if (allMenusByCategoryId.size() != 0) {
      throw new CustomException(EXIST_MENU_BY_CATEGORY);
    }
  }

  @Override
  @Transactional
  public void delete(Long categoryId) {
    validationCategory(categoryId);
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
