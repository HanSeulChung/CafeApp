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
@RequestMapping("/menus")
public class MenuController {
  @Autowired
  private MenuService menuService;

  /**
   * 메뉴 전체 조회 Controller
   * @return List<MenuResponse>: 등록된 메뉴들 List 형식으로 반환
   */
  @GetMapping()
  public ResponseEntity<List<MenuResponse>> readAllMenus() {
    var result = MenuResponse.toResponse(menuService.viewAllMenus());
    return ResponseEntity.ok(result);
  }

  /**
   * 메뉴 카테고리 대분류로 조회 Controller
   * @param superCategory: 대분류 카테고리 이름 ex) 음식, 음료, 굿즈
   * @return List<MenuResponse>: 해당 카테고리에 설정되어있는 메뉴 List로 반환
   * @throws Exception: 해당 대분류 카테고리 이름이 없을 경우 CustomException 발생
   */
  @GetMapping("/superCategory/{superCategory}")
  public ResponseEntity<List<MenuResponse>> readAllMenusBySuperCategory(@PathVariable("superCategory") String superCategory) {
    var result = MenuResponse.toResponse(menuService.viewAllBySuperCategory(superCategory));
    return ResponseEntity.ok(result);
  }

  /**
   * 메뉴 카테고리 중분류로 조회 Controller
   * @param baseCategory: 중분류 카테고리 이름 ex) 케이크, 샌드위치, 에스프레소, 텀블러
   * @return List<MenuResponse>: 해당 카테고리에 설정되어있는 메뉴들 List로 반환
   * @throws Exception: 해당 중분류 카테고리 이름이 없을 경우 CustomException 발생
   */
  @GetMapping("/baseCategory/{baseCategory}")
  public ResponseEntity<List<MenuResponse>> readAllMenusByBaseCategory(@PathVariable("baseCategory") String baseCategory) {
    var result = MenuResponse.toResponse(menuService.viewAllByBaseCategory(baseCategory));
    return ResponseEntity.ok(result);
  }

}