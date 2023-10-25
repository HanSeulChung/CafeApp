package com.chs.cafeapp.menu.controller;


import com.chs.cafeapp.menu.dto.MenuResponse;
import com.chs.cafeapp.menu.service.MenuService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/menu")
public class MenuController {
  @Autowired
  private MenuService menuService;

  /**
   * 메뉴 전체 조회
   */
  @GetMapping()
  public ResponseEntity<?> readAllMenus() {
    var result = MenuResponse.toResponse(menuService.viewAllMenus());
    return ResponseEntity.ok(result);
  }

  /**
   * 메뉴 카테고리 대분류로 조회
   */
  @GetMapping("/superCategory/{superCategory}")
  public ResponseEntity<?> readAllMenusBySuperCategory(@PathVariable("superCategory") String superCategory) {
    var result = MenuResponse.toResponse(menuService.viewAllBySuperCategory(superCategory));
    return ResponseEntity.ok(result);
  }

  /**
   * 메뉴 카테고리 중분류로 조회
   */
  @GetMapping("/baseCategory/{baseCategory}")
  public ResponseEntity<?> readAllMenusByBaseCategory(@PathVariable("baseCategory") String baseCategory) {
    var result = MenuResponse.toResponse(menuService.viewAllByBaseCategory(baseCategory));
    return ResponseEntity.ok(result);
  }

}