package com.chs.cafeapp.menu.service.impl;

import com.chs.cafeapp.menu.dto.MenuDto;
import com.chs.cafeapp.menu.dto.MenuInput;
import com.chs.cafeapp.menu.entity.Menus;
import com.chs.cafeapp.menu.repository.MenuRepository;
import com.chs.cafeapp.menu.service.MenuService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class MenuServiceImpl implements MenuService {
  private final MenuRepository menuRepository;

  @Override
  public MenuDto add(MenuInput menuInput) {
    boolean existMenus = menuRepository.existsByName(menuInput.getName());

    if (existMenus) {
      throw new RuntimeException("이미 있는 메뉴 이름입니다.");
    }
    MenuDto menuDto = MenuDto.fromInput(menuInput);
    Menus menu = Menus.toEntity(menuDto);
    menuRepository.save(menu);

    return menuDto;
  }

  @Override
  public MenuDto edit(MenuInput menuInput) {
    Menus menu = menuRepository.findById(menuInput.getId())
                  .orElseThrow(() -> new RuntimeException("메뉴가 존재하지 않습니다."));

    Menus menuEntity = menuRepository.findMenusByName(menuInput.getName()).orElseThrow(() -> new RuntimeException("존재하지 않는 메뉴 이름입니다."));

    return MenuDto.of(menuRepository.save(
                        Menus.builder()
                            .id(menuEntity.getId())
                            .menuType(menuInput.getMenuType())
                            .name(menuInput.getName())
                            .kcal(menuInput.getKcal())
                            .description(menuInput.getDescription())
                            .stock(menuInput.getStock())
                            .status(menuInput.getStatus())
                            .price(menuInput.getPrice())
                            .build()));
  }

  @Override
  public void delete(Long menuId) {
    Menus menu = menuRepository.findById(menuId)
                                .orElseThrow(() -> new RuntimeException("메뉴가 존재하지 않습니다."));
    menuRepository.deleteById(menuId);
  }
}
