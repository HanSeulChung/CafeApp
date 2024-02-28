package com.chs.cafeapp.domain.menu.service;

import com.chs.cafeapp.domain.menu.dto.MenuChangeStockQuantity;
import com.chs.cafeapp.domain.menu.dto.MenuDetail;
import com.chs.cafeapp.domain.menu.dto.MenuDto;
import com.chs.cafeapp.domain.menu.dto.MenuEditInput;
import com.chs.cafeapp.domain.menu.dto.MenuInput;
import com.chs.cafeapp.domain.menu.dto.MenuResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
  Page<MenuResponse> viewAllMenus(Pageable pageable);

  /**
   * 메뉴 카테고리 대분류로 조회
   */
  Page<MenuResponse> viewAllBySuperCategory(String superCategory, Pageable pageable);

  /**
   * 메뉴 카테고리 중분류로 조회
   */
  Page<MenuResponse> viewAllByBaseCategory(String baseCategory, Pageable pageable);

  /**
   * 메뉴 자세히 조회
   */
  MenuDetail viewDetailMenu(Long menuId);

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
