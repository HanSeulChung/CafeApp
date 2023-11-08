package com.chs.cafeapp.menu.controller;


import com.chs.cafeapp.exception.CustomException;
import com.chs.cafeapp.menu.dto.MenuDetail;
import com.chs.cafeapp.menu.dto.MenuResponse;
import com.chs.cafeapp.menu.service.MenuService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
   * @return Page<MenuResponse>: 등록된 메뉴들 Page 형식으로 반환
   */
  @GetMapping()
  public ResponseEntity<Page<MenuResponse>> readAllMenus(Pageable pageable) {
    return ResponseEntity.ok(menuService.viewAllMenus(pageable));
  }

  /**
   * 메뉴 카테고리 대분류로 조회 Controller
   * @param superCategory: 대분류 카테고리 이름 ex) 음식, 음료, 굿즈
   * @return Page<MenuResponse>: 해당 카테고리에 설정되어있는 메뉴 Page 반환
   * @throws CustomException: 해당 대분류 카테고리 이름이 없을 경우 CustomException 발생
   */
  @GetMapping("superCategory/{superCategory}")
  public ResponseEntity<Page<MenuResponse>> readAllMenusBySuperCategory(
      @PathVariable("superCategory") String superCategory,
      Pageable pageable) {
    return ResponseEntity.ok(menuService.viewAllBySuperCategory(superCategory, pageable));
  }

  /**
   * 메뉴 카테고리 중분류로 조회 Controller
   * @param baseCategory: 중분류 카테고리 이름 ex) 케이크, 샌드위치, 에스프레소, 텀블러
   * @return Page<MenuResponse>: 해당 카테고리에 설정되어있는 메뉴들 Page로 반환
   * @throws CustomException: 해당 중분류 카테고리 이름이 없을 경우 CustomException 발생
   */
  @GetMapping("/baseCategory/{baseCategory}")
  public ResponseEntity<Page<MenuResponse>> readAllMenusByBaseCategory(
      @PathVariable("baseCategory") String baseCategory,
      Pageable pageable) {
    return ResponseEntity.ok(menuService.viewAllByBaseCategory(baseCategory, pageable));
  }

  /**
   *
   * @param menuId: 자세히 볼 메뉴 id
   * @return MenuDetail: Menu id, name, kcal, description, stock, price, .. createdDateTime, updatedDateTime 등 상세 객체
   * @throws CustomException: menu id가 존재하지 않을경우, 해당 menu의 category가 존재하지 않을 경우 CustomException 발생
   */
  @GetMapping("/detail/menuId/{menuId}")
  public ResponseEntity<MenuDetail> readDetailMenu(@PathVariable Long menuId) {
    return ResponseEntity.ok(menuService.viewDetailMenu(menuId));
  }
}