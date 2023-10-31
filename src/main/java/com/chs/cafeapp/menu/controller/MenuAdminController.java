package com.chs.cafeapp.menu.controller;

import com.chs.cafeapp.menu.dto.MenuDto;
import com.chs.cafeapp.menu.dto.MenuEditInput;
import com.chs.cafeapp.menu.dto.MenuInput;
import com.chs.cafeapp.menu.dto.MenuResponse;
import com.chs.cafeapp.menu.service.MenuService;
import java.util.List;
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
import com.chs.cafeapp.exception.CustomException;

/**
 * 메뉴 CRUD Controller
 */
@RestController
@RequestMapping("/admin/menus")
public class MenuAdminController {

  @Autowired
  private MenuService menuService;

  /**
   * 메뉴 생성 Controller
   * @param  request: 메뉴 생성 입력 값
   * @return MenuResponse: 생성된 메뉴 정보(이름, 칼로리, 설명, 수량 ..)
   * @throws CustomException: 이미 있는 메뉴일 경우, 메뉴에 설정할 카테고리가 없을 경우 CustomException 발생
   */
  @PostMapping()
  public MenuResponse createMenu(@RequestBody MenuInput request) {
    MenuDto menuDto = menuService.add(request);
    var result = MenuResponse.toResponse(menuDto);
    return result;
  }

  /**
   * 메뉴 수정 Controller
   * @param  request: 메뉴 수정 입력 값
   * @return MenuResponse: 수정된 메뉴 정보(이름, 칼로리, 설명, 수량 ..)
   * @throws CustomException: 수정할 메뉴 이름이 이미 있는 메뉴일 경우, 수정할 카테고리가 없을 경우 CustomException 발생
   */
  @PutMapping()
  public MenuResponse editMenu(@RequestBody MenuEditInput request) {
    MenuDto menuDto = menuService.edit(request);
    var result = MenuResponse.toResponse(menuDto);
    return result;
  }

  /**
   * 메뉴 삭제 Controller
   * @param  menuId: 삭제할 메뉴 id
   * @return String: "해당 메뉴를 삭제했습니다."
   * @throws CustomException: 메뉴 id가 존재하지 않는 경우 CustomException 발생
   */
  @DeleteMapping()
  public ResponseEntity<String> deleteMenu(@RequestParam Long menuId) {
    menuService.delete(menuId);
    return ResponseEntity.ok("해당 메뉴를 삭제했습니다.");
  }

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
   * @return 해당 카테고리에 설정되어있는 메뉴 List로 반환
   * @throws CustomException: 해당 대분류 카테고리 이름이 없을 경우 CustomException 발생
   */
  @GetMapping("superCategory/{superCategory}")
  public ResponseEntity<List<MenuResponse>> readAllMenusBySuperCategory(@PathVariable("superCategory") String superCategory) {
    var result = MenuResponse.toResponse(menuService.viewAllBySuperCategory(superCategory));
    return ResponseEntity.ok(result);
  }

  /**
   * 메뉴 카테고리 중분류로 조회 Controller
   * @param baseCategory: 중분류 카테고리 이름 ex) 케이크, 샌드위치, 에스프레소, 텀블러
   * @return 해당 카테고리에 설정되어있는 메뉴들 List로 반환
   * @throws CustomException: 해당 중분류 카테고리 이름이 없을 경우 CustomException 발생
   */
  @GetMapping("/baseCategory/{baseCategory}")
  public ResponseEntity<List<MenuResponse>> readAllMenusByBaseCategory(@PathVariable("baseCategory") String baseCategory) {
    var result = MenuResponse.toResponse(menuService.viewAllByBaseCategory(baseCategory));
    return ResponseEntity.ok(result);
  }

  /**
   * 메뉴 품절 상태
   * @param menuId: 품절한 메뉴 id
   * @return MenuDto: 품절시킨 MenuDto
   * @throws CustomException: 해당 id를 가진 메뉴가 없을 경우, 이미 품절 상태인 경우 CustomException 발생
   */
  @PatchMapping("/soldout")
  public ResponseEntity<MenuDto> soldOut(@RequestParam Long menuId) {
    return ResponseEntity.ok(menuService.changeToSoldOut(menuId));
  }

  /**
   * 품절된 메뉴 다시 판매
   * @param menuId: 품절됐었던, 다시 판매할 메뉴 id
   * @return MenuDto: 다시 판매시킨 MenuDto
   * @throws CustomException: 해당 id를 가진 메뉴가 없을 경우, 이미 판매 중인 경우 CustomException 발생
   */
  @PatchMapping("/sale")
  public ResponseEntity<MenuDto> sold(@RequestParam Long menuId) {
    return ResponseEntity.ok(menuService.changeToSale(menuId));
  }

}
