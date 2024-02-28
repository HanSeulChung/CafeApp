package com.chs.cafeapp.service.menu;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


import com.chs.cafeapp.domain.menu.category.entity.Category;
import com.chs.cafeapp.domain.menu.dto.MenuChangeStockQuantity;
import com.chs.cafeapp.domain.menu.dto.MenuDto;
import com.chs.cafeapp.domain.menu.entity.Menus;
import com.chs.cafeapp.domain.menu.repository.MenuRepository;
import com.chs.cafeapp.domain.menu.service.impl.MenuServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MenuServiceImplTest {
  @Mock
  private MenuRepository menuRepository;
  @InjectMocks
  private MenuServiceImpl menuService;

  @Test
  @DisplayName("메뉴 수량 추가")
  void changeStockQuantity_Success1() {
    //given
    Menus menu = Menus.builder()
        .id(1L)
        .name("맛있는 닭가슴살 샌드위치")
        .kcal(500)
        .description("특제 비법 소스를 넣어 굉장히 맛있고 건강한 샌드위치 입니다.")
        .stock(20)
        .price(10)
        .isSoldOut(false)
        .build();
    menu.setCategory(new Category());

    when(menuRepository.findById(1L)).thenReturn(Optional.of(menu));
    when(menuRepository.save(any(Menus.class))).thenReturn(menu);

    //when
    MenuChangeStockQuantity menuChangeStockQuantity = new MenuChangeStockQuantity(1L, 10);
    MenuDto menuDto = menuService.changeStockQuantity(menuChangeStockQuantity);

    //then
    assertNotNull(menuDto);
    assertEquals(menuDto.getStock(), 20 + 10);
  }

  @Test
  @DisplayName("메뉴 수량 감소")
  void changeStockQuantity_Success2() {
    //given
    Menus menu = Menus.builder()
        .id(1L)
        .name("맛있는 닭가슴살 샌드위치")
        .kcal(500)
        .description("특제 비법 소스를 넣어 굉장히 맛있고 건강한 샌드위치 입니다.")
        .stock(20)
        .price(10)
        .isSoldOut(false)
        .build();

    menu.setCategory(new Category());

    when(menuRepository.findById(1L)).thenReturn(Optional.of(menu));
    when(menuRepository.save(any(Menus.class))).thenReturn(menu);

    //when
    MenuChangeStockQuantity menuChangeStockQuantity = new MenuChangeStockQuantity(1L, -5);
    MenuDto menuDto = menuService.changeStockQuantity(menuChangeStockQuantity);

    //then
    assertNotNull(menuDto);
    assertEquals(menuDto.getStock(), 20 - 5);
  }
  @Test
  @DisplayName("품절된 메뉴 수량 추가시 판매중으로 변환")
  void changeStockQuantity_setSoldout() {
    //given
    Menus menu = Menus.builder()
        .id(1L)
        .name("맛있는 닭가슴살 샌드위치")
        .kcal(500)
        .description("특제 비법 소스를 넣어 굉장히 맛있고 건강한 샌드위치 입니다.")
        .stock(0)
        .price(10)
        .isSoldOut(true)
        .build();

    menu.setCategory(new Category());

    when(menuRepository.findById(1L)).thenReturn(Optional.of(menu));
    when(menuRepository.save(any(Menus.class))).thenReturn(menu);

    //when
    MenuChangeStockQuantity menuChangeStockQuantity = new MenuChangeStockQuantity(1L, 5);
    MenuDto menuDto = menuService.changeStockQuantity(menuChangeStockQuantity);

    //then
    assertNotNull(menuDto);
    assertEquals(menuDto.getStock(), 0 + 5);
    assertEquals(menuDto.isSoldOut(), false);
  }

  @Test
  @DisplayName("판매중인 메뉴 수량 줄였을 때 0일 경우 품절으로 변환")
  void changeStockQuantity_setSale() {
    //given
    Menus menu = Menus.builder()
        .id(1L)
        .name("맛있는 닭가슴살 샌드위치")
        .kcal(500)
        .description("특제 비법 소스를 넣어 굉장히 맛있고 건강한 샌드위치 입니다.")
        .stock(5)
        .price(10)
        .isSoldOut(false)
        .build();

    menu.setCategory(new Category());

    when(menuRepository.findById(1L)).thenReturn(Optional.of(menu));
    when(menuRepository.save(any(Menus.class))).thenReturn(menu);

    //when
    MenuChangeStockQuantity menuChangeStockQuantity = new MenuChangeStockQuantity(1L, -5);
    MenuDto menuDto = menuService.changeStockQuantity(menuChangeStockQuantity);

    //then
    assertNotNull(menuDto);
    assertEquals(menuDto.getStock(), 5 - 5);
    assertEquals(menuDto.isSoldOut(), true);
  }
}