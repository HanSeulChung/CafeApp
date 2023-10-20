package com.chs.cafeapp.menu.category.controller;

import com.chs.cafeapp.menu.category.dto.CategoryDto;
import com.chs.cafeapp.menu.category.dto.CategoryEditInput;
import com.chs.cafeapp.menu.category.dto.CategoryInput;
import com.chs.cafeapp.menu.category.dto.CategoryResponse;
import com.chs.cafeapp.menu.category.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 카테고리 CRUD Controller
 */
@RestController
@RequestMapping("admin/category")
public class CategoryController {
  @Autowired
  private CategoryService categoryService;

  /**
   * 카테고리 생성 Controller
   */
  @PostMapping()
  public CategoryResponse addCategory(@RequestBody CategoryInput request) {
    CategoryDto categoryDto = categoryService.add(request);
    var result = CategoryResponse.toResponse(categoryDto);
    return result;
  }

  /**
   * 카테고리 수정 Controller
   */
  @PutMapping()
  public CategoryResponse editCategory(@RequestBody CategoryEditInput categoryEditInput) {
    CategoryDto categoryDto = categoryService.edit(categoryEditInput);
    var result = CategoryResponse.toResponse(categoryDto);
    return result;
  }

  /**
   * 카테고리 삭제 Controller
   */
  @DeleteMapping()
  public ResponseEntity<?> deleteCategory(@RequestParam Long categoryId) {
    categoryService.delete(categoryId);
    return ResponseEntity.ok("해당 카테고리를 삭제했습니다.");
  }

  /**
   * 카테고리 전체 조회
   */
  @GetMapping()
  public ResponseEntity<?> readAllCategory() {
    var result = CategoryResponse.toResponse(categoryService.readAll());
    return ResponseEntity.ok(result);
  }

  /**
   * 카테고리 대분류로 조회
   */
  @GetMapping("{superCategory}")
  public ResponseEntity<?> readSuperCategory(@PathVariable("superCategory") String superCategory) {
    var result = CategoryResponse.toResponse(categoryService.readSuperCategory(superCategory));
    return ResponseEntity.ok(result);
  }
}
