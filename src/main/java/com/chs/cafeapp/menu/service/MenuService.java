package com.chs.cafeapp.menu.service;

import com.chs.cafeapp.menu.dto.MenuDto;
import com.chs.cafeapp.menu.dto.MenuInput;

public interface MenuService {
  /**
   * 메뉴 추가
   */
  MenuDto add(MenuInput menuInput);
  /**
   * 메뉴 수정
   */
  MenuDto edit(MenuInput menuInput);
  /**
   * 메뉴 삭제
   */
  void delete(Long menuId);
  /**
   * 메뉴 조회
   */

}
