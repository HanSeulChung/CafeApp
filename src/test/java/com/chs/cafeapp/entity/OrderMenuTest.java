package com.chs.cafeapp.entity;


import static org.junit.jupiter.api.Assertions.assertEquals;

import com.chs.cafeapp.menu.category.entity.Category;
import com.chs.cafeapp.menu.entity.Menus;
import com.chs.cafeapp.menu.repository.MenuRepository;
import com.chs.cafeapp.order.entity.Order;
import com.chs.cafeapp.order.entity.OrderedMenu;
import com.chs.cafeapp.order.repository.OrderRepository;
import com.chs.cafeapp.order.repository.OrderedMenuRepository;
import com.chs.cafeapp.order.type.OrderStatus;
import java.util.Arrays;
import javax.persistence.EntityManager;
import org.aspectj.weaver.ast.Or;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrderMenuTest {
  @Autowired
  private EntityManager em;
  @Autowired
  private MenuRepository menuRepository;
  @Autowired
  private OrderRepository orderRepository;
  @Autowired
  private OrderedMenuRepository orderedMenuRepository;


  @Test
  @DisplayName("음료 카테고리 메뉴 갯수 세기")
  void countOrderedMenuBySuperCategory_Success() {
      //given
    Category category1 = Category.builder()
        .id(1L)
        .superCategory("음료")
        .baseCategory("에스프레소")
        .build();

    Category category2 = Category.builder()
        .id(1L)
        .superCategory("음식")
        .baseCategory("샌드위치")
        .build();

    Category category3 = Category.builder()
        .id(1L)
        .superCategory("음료")
        .baseCategory("스무디")
        .build();

    Menus menus1 = Menus.builder()
        .id(1L)
        .category(category1)
        .build();

    Menus menus2 = Menus.builder()
        .id(2L)
        .category(category2)
        .build();

    Menus menus3 = Menus.builder()
        .id(3L)
        .category(category3)
        .build();

    OrderedMenu orderedMenu1 = OrderedMenu.builder()
        .menus(menus1)
        .quantity(3)
        .build();

    OrderedMenu orderedMenu2 = OrderedMenu.builder()
        .menus(menus2)
        .quantity(2)
        .build();

    OrderedMenu orderedMenu3 = OrderedMenu.builder()
        .menus(menus3)
        .quantity(1)
        .build();

    Order order = Order.builder()
        .id(1L)
        .orderStatus(OrderStatus.PickUpSuccess)
        .orderedMenus(Arrays.asList(orderedMenu1, orderedMenu2, orderedMenu3))
        .build();
      //when
    long drinksCnt = order.getOrderedMenus().stream()
        .filter(orderedMenu -> "음료".equals(orderedMenu.getMenus().getCategory().getSuperCategory()))
        .mapToLong(orderedMenu -> orderedMenu.getQuantity()) // quantity 필드 추출
        .sum();

    // then
    assertEquals(4, drinksCnt);
  }

}
