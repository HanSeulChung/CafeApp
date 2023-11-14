package com.chs.cafeapp.menu.category.controller;

import com.chs.cafeapp.menu.category.dto.CategoryDto;
import com.chs.cafeapp.menu.category.dto.CategoryEditInput;
import com.chs.cafeapp.menu.category.dto.CategoryInput;
import com.chs.cafeapp.menu.category.dto.CategoryResponse;
import com.chs.cafeapp.menu.category.service.CategoryService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.chs.cafeapp.exception.CustomException;

/**
 * 카테고리 CRUD Controller
 */
@RestController
@PreAuthorize("ROLE_ADMIN")
@RequestMapping("/admin/category")
public class CategoryController {
  @Autowired
  private CategoryService categoryService;

  /**
   * 카테고리 생성 Controller
   * @param categoryInput 등록할 category id와 변경할 대분류, 중분류 카테고리 이름
   * @return CategoryResponse: 등록한 대분류, 중분류 category 반환
   * @throws CustomException: 등록할 대분류, 중분류 category가 이미 존재할 때 CustomException 발생
   */
  @PostMapping()
  public CategoryResponse addCategory(@RequestBody CategoryInput categoryInput) {
    CategoryDto categoryDto = categoryService.add(categoryInput);
    var result = CategoryResponse.toResponse(categoryDto);
    return result;
  }

  /**
   * 카테고리 수정 Controller
   * @param categoryEditInput: 수정할 category id와 변경할 대분류, 중분류 카테고리 이름
   * @return CategoryResponse: 수정한 대분류, 중분류 category 반환
   * @throws CustomException: 수정한 대분류, 중분류 category가 이미 존재할 때,
   *                          category id가 없을 경우 CustomException 발생
   */
  @PutMapping()
  public CategoryResponse editCategory(@RequestBody CategoryEditInput categoryEditInput) {
    CategoryDto categoryDto = categoryService.edit(categoryEditInput);
    var result = CategoryResponse.toResponse(categoryDto);
    return result;
  }

  /**
   * 카테고리 삭제 Controller
   * @param categoryId: 삭제할 카테고리 id
   * @return String "해당 카테고리를 삭제했습니다."
   * @throws CustomException: 카테고리 id가 존재하지 않는 경우 CustomException 발생
   */
  @DeleteMapping()
  public ResponseEntity<String> deleteCategory(@RequestParam Long categoryId) {
    categoryService.delete(categoryId);
    return ResponseEntity.ok("해당 카테고리를 삭제했습니다.");
  }

  /**
   * 카테고리 전체 조회 Controller
   * @return List<CategoryResponse>: 등록되어있는 카테고리 전체 반환됨
   */
  @GetMapping()
  public ResponseEntity<List<CategoryResponse>> readAllCategory() {
    var result = CategoryResponse.toResponse(categoryService.readAll());
    return ResponseEntity.ok(result);
  }

  /**
   * 카테고리 대분류로 조회 Controller
   * @param superCategory: 조회할 대분류 카테고리 이름
   * @return List<CategoryResponse>: 해당 대분류 카테고리 이름으로 등록되어있는 카테고리들이 List로 반환됨
   */
  @GetMapping("{superCategory}")
  public ResponseEntity<List<CategoryResponse>> readSuperCategory(@PathVariable("superCategory") String superCategory) {
    var result = CategoryResponse.toResponse(categoryService.readSuperCategory(superCategory));
    return ResponseEntity.ok(result);
  }
}
