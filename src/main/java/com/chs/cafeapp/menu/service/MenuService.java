package com.chs.cafeapp.menu.service;

import com.chs.cafeapp.menu.dto.MenuChangeStockQuantity;
import com.chs.cafeapp.menu.dto.MenuDto;
import com.chs.cafeapp.menu.dto.MenuEditInput;
import com.chs.cafeapp.menu.dto.MenuInput;
import java.util.List;

public interface MenuService {
  /**
   * 메뉴 추가
   */
  MenuDto add(MenuInput menuInput);

  /**
   * 메뉴 수정
   */
  MenuDto edit(MenuEditInput menuEditInput);

  /**
   * 메뉴 삭제
   */
  void delete(Long menuId);

  /**
   * 메뉴 전체 조회
   */
  List<MenuDto> viewAllMenus();

  /**
   * 메뉴 카테고리 대분류로 조회
   */
  List<MenuDto> viewAllBySuperCategory(String superCategory);

  /**
   * 메뉴 카테고리 중분류로 조회
   */
  List<MenuDto> viewAllByBaseCategory(String baseCategory);

  /**
   * 품절으로 상태 변경
   */
  MenuDto changeToSoldOut(Long menuId);

  /**
   * 판매중으로 상태 변경
   */
  MenuDto changeToSale(Long menuId);

  /**
   * 메뉴 수량 변경
   */
  MenuDto changeStockQuantity(MenuChangeStockQuantity menuChangeStockQuantity);
}
