package com.chs.cafeapp.menu.controller;

import com.chs.cafeapp.menu.dto.MenuDto;
import com.chs.cafeapp.menu.dto.MenuEditInput;
import com.chs.cafeapp.menu.dto.MenuInput;
import com.chs.cafeapp.menu.dto.MenuResponse;
import com.chs.cafeapp.menu.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 메뉴 CRUD Controller
 */
@RestController
@RequestMapping("/admin/menu")
public class MenuAdminController {

  @Autowired
  private MenuService menuService;

  /**
   * 메뉴 추가 Controller
   */
  @PostMapping()
  public MenuResponse addMenu(@RequestBody MenuInput request) {
    MenuDto menuDto = menuService.add(request);
    var result = MenuResponse.toResponse(menuDto);
    return result;
  }

  /**
   * 메뉴 수정 Controller
   */
  @PutMapping()
  public MenuResponse editMenu(@RequestBody MenuEditInput request) {
    MenuDto menuDto = menuService.edit(request);
    var result = MenuResponse.toResponse(menuDto);
    return result;
  }

  /**
   * 메뉴 삭제 Controller
   */
  @DeleteMapping()
  public ResponseEntity<?> deleteMenu(@RequestParam Long menuId) {
    menuService.delete(menuId);
    return ResponseEntity.ok("해당 메뉴를 삭제했습니다.");
  }

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
  @GetMapping("superCategory/{superCategory}")
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

  /**
   * 메뉴 품절 상태
   */
  @PatchMapping("/soldout")
  public ResponseEntity<?> soldOut(@RequestParam Long menuId) {
    return ResponseEntity.ok(menuService.changeToSoldOut(menuId));
  }

  /**
   * 품절된 메뉴 다시 판매
   */
  @PatchMapping("/sale")
  public ResponseEntity<?> sold(@RequestParam Long menuId) {
    return ResponseEntity.ok(menuService.changeToSale(menuId));
  }

}
