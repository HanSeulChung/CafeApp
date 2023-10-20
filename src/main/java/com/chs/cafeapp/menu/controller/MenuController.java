package com.chs.cafeapp.menu.controller;

import com.chs.cafeapp.menu.dto.MenuDto;
import com.chs.cafeapp.menu.dto.MenuInput;
import com.chs.cafeapp.menu.dto.MenuResponse;
import com.chs.cafeapp.menu.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
public class MenuController {

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
  public MenuResponse editMenu(@RequestBody MenuInput request) {
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


}