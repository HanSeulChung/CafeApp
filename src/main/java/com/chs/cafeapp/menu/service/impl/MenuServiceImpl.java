package com.chs.cafeapp.menu.service.impl;

import static com.chs.cafeapp.exception.type.ErrorCode.ALREADY_SALE;
import static com.chs.cafeapp.exception.type.ErrorCode.ALREADY_SOLD_OUT;
import static com.chs.cafeapp.exception.type.ErrorCode.CAN_NOT_MINUS_THAN_STOCK;
import static com.chs.cafeapp.exception.type.ErrorCode.CATEGORY_NOT_FOUND;
import static com.chs.cafeapp.exception.type.ErrorCode.EXIST_MENU_NAME;
import static com.chs.cafeapp.exception.type.ErrorCode.MENU_NOT_FOUND;

import com.chs.cafeapp.exception.CustomException;
import com.chs.cafeapp.menu.category.entity.Category;
import com.chs.cafeapp.menu.category.repository.CategoryRepository;
import com.chs.cafeapp.menu.dto.MenuChangeStockQuantity;
import com.chs.cafeapp.menu.dto.MenuDetail;
import com.chs.cafeapp.menu.dto.MenuDto;
import com.chs.cafeapp.menu.dto.MenuEditInput;
import com.chs.cafeapp.menu.dto.MenuInput;
import com.chs.cafeapp.menu.dto.MenuResponse;
import com.chs.cafeapp.menu.entity.Menus;
import com.chs.cafeapp.menu.repository.MenuRepository;
import com.chs.cafeapp.menu.service.MenuService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class MenuServiceImpl implements MenuService {

  private final MenuRepository menuRepository;
  private final CategoryRepository categoryRepository;

  public Menus validationMenus(Long menuId) {
    Menus menus = menuRepository.findById(menuId)
        .orElseThrow(() -> new CustomException(MENU_NOT_FOUND));

    Category category = categoryRepository.findById(menus.getCategory().getId())
        .orElseThrow(() -> new CustomException(CATEGORY_NOT_FOUND));

    return menus;
  }

  @Override
  public MenuDto add(MenuInput menuInput) {
    boolean existMenus = menuRepository.existsByName(menuInput.getName());

    if (existMenus) {
      throw new CustomException(EXIST_MENU_NAME);
    }

    MenuDto menuDto = MenuDto.fromInput(menuInput);
    Category category = categoryRepository.findBySuperCategoryAndBaseCategory(
            menuInput.getSuperCategory(), menuInput.getBaseCategory())
        .orElseThrow(() -> new CustomException(CATEGORY_NOT_FOUND));

    Menus menu = Menus.toEntity(menuDto);
    menu.setCategory(category);
    menuRepository.save(menu);
    menuDto.setSuperCategory(menu.getCategory().getSuperCategory());
    menuDto.setBaseCategory(menu.getCategory().getBaseCategory());

    return menuDto;
  }

  @Override
  public MenuDto edit(MenuEditInput menuEditInput) {
    Menus menu = validationMenus(menuEditInput.getId());

    Category category = categoryRepository.findBySuperCategoryAndBaseCategory(
            menuEditInput.getSuperCategory(), menuEditInput.getBaseCategory())
        .orElseThrow(() -> new CustomException(CATEGORY_NOT_FOUND));

    if (!menuEditInput.getName().equals(menu.getName())) {
      boolean existMenu = menuRepository.existsByName(menuEditInput.getName());
      if (existMenu) {
        throw new CustomException(EXIST_MENU_NAME);
      }
    }

    Menus buildMenu = Menus.builder()
        .id(menu.getId())
        .category(category)
        .name(menuEditInput.getName())
        .kcal(menuEditInput.getKcal())
        .description(menuEditInput.getDescription())
        .stock(menuEditInput.getStock())
        .price(menuEditInput.getPrice())
        .build();

    return MenuDto.of(menuRepository.save(buildMenu));
  }

  @Override
  public void delete(Long menuId) {
    menuRepository.findById(menuId)
                    .orElseThrow(() -> new CustomException(MENU_NOT_FOUND));
    menuRepository.deleteById(menuId);
  }

  @Override
  public Page<MenuResponse> viewAllMenus(Pageable pageable) {
    Page<Menus> menusPage = menuRepository.findAll(pageable);
    List<MenuDto> menuDtoList = MenuDto.convertListDtoFromPageEntity(menusPage);
    List<MenuResponse> responseList = MenuResponse.toResponse(menuDtoList);
    return new PageImpl<>(responseList, menusPage.getPageable(), menusPage.getTotalElements());
  }

  @Override
  public Page<MenuResponse> viewAllBySuperCategory(String superCategory, Pageable pageable) {
    List<Long> categoryIds = categoryRepository.findIdsBySuperCategory(superCategory);
    if (categoryIds == null) {
      throw new CustomException(CATEGORY_NOT_FOUND);
    }

    Page<Menus> allByCategoryIdIn = menuRepository.findAllByCategoryIdIn(categoryIds, pageable);
    List<MenuDto> menuDtoList = MenuDto.convertListDtoFromPageEntity(allByCategoryIdIn);
    List<MenuResponse> responseList = MenuResponse.toResponse(menuDtoList);
    return new PageImpl<>(responseList, allByCategoryIdIn.getPageable(), allByCategoryIdIn.getTotalElements());
  }

  @Override
  public Page<MenuResponse> viewAllByBaseCategory(String baseCategory, Pageable pageable) {

    List<Long> categoryIds = categoryRepository.findIdsByBaseCategory(baseCategory);
    if (categoryIds == null) {
      throw new CustomException(CATEGORY_NOT_FOUND);
    }

    Page<Menus> allByCategoryIdIn = menuRepository.findAllByCategoryIdIn(categoryIds, pageable);
    List<MenuDto> menuDtoList = MenuDto.convertListDtoFromPageEntity(allByCategoryIdIn);
    List<MenuResponse> responseList = MenuResponse.toResponse(menuDtoList);
    return new PageImpl<>(responseList, allByCategoryIdIn.getPageable(), allByCategoryIdIn.getTotalElements());
  }

  @Override
  public MenuDetail viewDetailMenu(Long menuId) {
    Menus menus = validationMenus(menuId);
    return MenuDetail.toDetail(MenuDto.of(menus));
  }

  @Override
  public MenuDto changeToSoldOut(Long menuId) {
    Menus menu = validationMenus(menuId);

    if (menu.isSoldOut()) {
      throw new CustomException(ALREADY_SOLD_OUT);
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
    Menus menu = validationMenus(menuId);

    if (!menu.isSoldOut()) {
      throw new CustomException(ALREADY_SALE);
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
    Menus menus = validationMenus(menuChangeStockQuantity.getMenuId());

    if (menus.getStock() + menuChangeStockQuantity.getQuantity() < 0) {
      throw new CustomException(CAN_NOT_MINUS_THAN_STOCK);
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
