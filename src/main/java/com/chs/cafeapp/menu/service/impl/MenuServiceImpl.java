package com.chs.cafeapp.menu.service.impl;

import com.chs.cafeapp.menu.category.entity.Category;
import com.chs.cafeapp.menu.category.repository.CategoryRepository;
import com.chs.cafeapp.menu.dto.MenuChangeStockQuantity;
import com.chs.cafeapp.menu.dto.MenuDto;
import com.chs.cafeapp.menu.dto.MenuEditInput;
import com.chs.cafeapp.menu.dto.MenuInput;
import com.chs.cafeapp.menu.entity.Menus;
import com.chs.cafeapp.menu.repository.MenuRepository;
import com.chs.cafeapp.menu.service.MenuService;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.awt.Menu;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class MenuServiceImpl implements MenuService {

  private final MenuRepository menuRepository;
  private final CategoryRepository categoryRepository;
  @Override
  public MenuDto add(MenuInput menuInput) {
    boolean existMenus = menuRepository.existsByName(menuInput.getName());

    if (existMenus) {
      throw new RuntimeException("이미 있는 메뉴 이름입니다.");
    }

    MenuDto menuDto = MenuDto.fromInput(menuInput);
    Category category = categoryRepository.findBySuperCategoryAndBaseCategory(
            menuInput.getSuperCategory(), menuInput.getBaseCategory())
        .orElseThrow(() -> new RuntimeException("해당되는 카테고리가 없습니다. 카테고리 등록 후 메뉴를 등록해주세요."));

    Menus menu = Menus.toEntity(menuDto);
    menu.setCategory(category);
    menuRepository.save(menu);

    menuDto.setSuperCategory(menu.getCategory().getSuperCategory());
    menuDto.setBaseCategory(menu.getCategory().getBaseCategory());

    return menuDto;
  }

  @Override
  public MenuDto edit(MenuEditInput menuEditInput) {
    Menus menu = menuRepository.findById(menuEditInput.getId())
                  .orElseThrow(() -> new RuntimeException("메뉴가 존재하지 않습니다."));

    Category category = categoryRepository.findBySuperCategoryAndBaseCategory(
            menuEditInput.getSuperCategory(), menuEditInput.getBaseCategory())
        .orElseThrow(() -> new RuntimeException("해당되는 카테고리가 없습니다. 카테고리 등록 후 메뉴를 등록해주세요."));

    if (!menuEditInput.getName().equals(menu.getName())) {
      boolean existMenu = menuRepository.existsByName(menuEditInput.getName());
      if (existMenu) {
        throw new RuntimeException("이미 존재하는 메뉴 이름입니다. 다시 확인 후 수정해주세요.");
      }
    }

    Menus buildMenu = Menus.builder()
        .id(menu.getId())
        .name(menuEditInput.getName())
        .kcal(menuEditInput.getKcal())
        .description(menuEditInput.getDescription())
        .stock(menuEditInput.getStock())
        .price(menuEditInput.getPrice())
        .build();

    buildMenu.setCategory(category);

    return MenuDto.of(menuRepository.save(buildMenu));
  }

  @Override
  public void delete(Long menuId) {
    Menus menu = menuRepository.findById(menuId)
                                .orElseThrow(() -> new RuntimeException("존재하지 않는 메뉴입니다."));
    menuRepository.deleteById(menuId);
  }

  @Override
  public List<MenuDto> viewAllMenus() {
    return MenuDto.of(menuRepository.findAll());
  }

  @Override
  public List<MenuDto> viewAllBySuperCategory(String superCategory) {
    List<Long> categoryIds = categoryRepository.findIdsBySuperCategory(superCategory);
    if (categoryIds == null) {
      throw new RuntimeException("해당 카테고리가 존재하지 않습니다.");
    }
    List<Menus> allByCategoryIdIn = menuRepository.findAllByCategoryIdIn(categoryIds);
    return MenuDto.of(allByCategoryIdIn);
  }

  @Override
  public List<MenuDto> viewAllByBaseCategory(String baseCategory) {

    List<Long> categoryIds = categoryRepository.findIdsByBaseCategory(baseCategory);
    if (categoryIds == null) {
      throw new RuntimeException("해당 카테고리가 존재하지 않습니다.");
    }
    List<Menus> allByCategoryIdIn = menuRepository.findAllByCategoryIdIn(categoryIds);
    return MenuDto.of(allByCategoryIdIn);
  }

  @Override
  public MenuDto changeToSoldOut(Long menuId) {
    Menus menu = menuRepository.findById(menuId)
        .orElseThrow(() -> new RuntimeException("해당 메뉴가 존재하지 않습니다."));

    if (menu.isSoldOut()) {
      throw new RuntimeException("이미 품절된 메뉴입니다.");
    }

    MenuDto menuDto = MenuDto.of(menu);
    menuDto.setSoldOut(true);
    Menus saveMenu = Menus.toEntity(menuDto);
    saveMenu.setCategory(menu.getCategory());
    menuRepository.save(saveMenu);
    return menuDto;
  }

  @Override
  public MenuDto changeToSale(Long menuId) {
    Menus menu = menuRepository.findById(menuId)
        .orElseThrow(() -> new RuntimeException("해당 메뉴가 존재하지 않습니다."));

    if (!menu.isSoldOut()) {
      throw new RuntimeException("이미 판매중인 메뉴입니다.");
    }

    MenuDto menuDto = MenuDto.of(menu);
    menuDto.setSoldOut(false);
    Menus saveMenu = Menus.toEntity(menuDto);
    saveMenu.setCategory(menu.getCategory());

    menuRepository.save(saveMenu);
    return menuDto;
  }

  @Override
  public MenuDto changeStockQuantity(MenuChangeStockQuantity menuChangeStockQuantity) {
    Menus menus = menuRepository.findById(menuChangeStockQuantity.getMenuId())
        .orElseThrow(() -> new RuntimeException("메뉴가 존재하지 않습니다."));

    if (menus.getStock() + menuChangeStockQuantity.getQuantity() < 0) {
      throw new RuntimeException("메뉴의 재고보다 더 줄일 수는 없습니다.");
    }

    menus.addStock(menuChangeStockQuantity.getQuantity());

    if (menus.getStock() == 0 && !menus.isSoldOut()) {
      menus.setSoldOut(true);
    }

    if (menus.getStock() > 0 && menus.isSoldOut()) {
      menus.setSoldOut(false);
    }
    Menus saveMenus = menuRepository.save(menus);
    return MenuDto.of(saveMenus);
  }
}
