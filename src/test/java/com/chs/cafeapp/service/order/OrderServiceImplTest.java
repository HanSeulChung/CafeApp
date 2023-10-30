package com.chs.cafeapp.service.order;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.chs.cafeapp.cart.entity.Cart;
import com.chs.cafeapp.cart.entity.CartMenu;
import com.chs.cafeapp.cart.repository.CartMenusRepository;
import com.chs.cafeapp.cart.repository.CartRepository;
import com.chs.cafeapp.cart.service.impl.CartMenuServiceImpl;
import com.chs.cafeapp.menu.entity.Menus;
import com.chs.cafeapp.menu.repository.MenuRepository;
import com.chs.cafeapp.order.dto.OrderAllFromCartInput;
import com.chs.cafeapp.order.dto.OrderDto;
import com.chs.cafeapp.order.entity.Order;
import com.chs.cafeapp.order.entity.OrderedMenu;
import com.chs.cafeapp.order.repository.OrderRepository;
import com.chs.cafeapp.order.repository.OrderedMenuRepository;
import com.chs.cafeapp.order.service.impl.OrderServiceImpl;
import com.chs.cafeapp.order.type.OrderStatus;
import com.chs.cafeapp.user.entity.User;
import com.chs.cafeapp.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {
  @Mock
  private UserRepository userRepository;

  @Mock
  private MenuRepository menuRepository;
  @Mock
  private CartRepository cartRepository;
  @Mock
  private OrderRepository orderRepository;
  @Mock
  private OrderedMenuRepository orderedMenuRepository;
  @Mock
  private CartMenusRepository cartMenusRepository;

  @InjectMocks
  private OrderServiceImpl orderService;

  @Mock
  private CartMenuServiceImpl cartMenuService;

  @Test
  @Transactional
  @DisplayName("장바구니 전체 메뉴 주문 성공")
  void orderAllFromCart_Success() {
    // given
    User user = User.builder()
        .id(4L)
        .loginId("user2@naver.com")
        .password("user2비밀번호")
        .userName("user2 이름")
        .nickName("user2 닉네임")
        .sex("Male")
        .age(32)
        .build();

    Menus menu1 = Menus.builder()
        .id(1L)
        .name("맛있는 닭가슴살 샌드위치")
        .kcal(500)
        .description("특제 비법 소스를 넣어 굉장히 맛있고 건강한 샌드위치 입니다.")
        .stock(10)
        .price(10000)
        .isSoldOut(false)
        .build();

    Menus menu2 = Menus.builder()
        .id(2L)
        .name("시원한 아이스 아메리카노")
        .kcal(8)
        .description("고소한 원두가 특징인 아이스 카페 아메리카노 입니다.")
        .stock(10)
        .price(4100)
        .isSoldOut(false)
        .build();

    CartMenu cartMenu1 = CartMenu.builder()
        .id(1L)
        .menus(menu1)
        .quantity(3)
        .build();

    CartMenu cartMenu2 = CartMenu.builder()
        .id(2L)
        .menus(menu2)
        .quantity(3)
        .build();

    Cart cart = Cart.builder()
        .id(1L)
        .cartMenu(Arrays.asList(cartMenu1, cartMenu2)) // Set cartMenu list
        .user(user)
        .build();

    Order order = Order.builder()
        .id(1L)
        .user(user)
        .build();

    cartMenu1.setCart(cart);
    cartMenu2.setCart(cart);
    user.setCart(cart);

    userRepository.save(user);
    menuRepository.save(menu1);
    menuRepository.save(menu2);
    cartRepository.save(cart);
    cartMenusRepository.save(cartMenu1);
    cartMenusRepository.save(cartMenu2);

    when(userRepository.findByLoginId("user2@naver.com")).thenReturn(Optional.of(user));
    when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
    when(menuRepository.findById(1L)).thenReturn(Optional.of(menu1));
    when(menuRepository.findById(2L)).thenReturn(Optional.of(menu2));
    when(cartMenusRepository.findAllByCartId(1L)).thenReturn(Arrays.asList(cartMenu1, cartMenu2));
    when(orderRepository.save(any(Order.class))).thenReturn(order);
    when(orderedMenuRepository.save(any(OrderedMenu.class))).thenReturn(any());
//    doNothing().when(cartMenuService).deleteAllCartMenu(anyLong(), anyString());
    // When
    OrderAllFromCartInput orderAllFromCartInput = new OrderAllFromCartInput(1L, false);
    OrderDto result = orderService.orderAllFromCart(orderAllFromCartInput, "user2@naver.com");

    // Then
    verify(cartMenuService, times(1)).deleteAllCartMenu(1L, "user2@naver.com");
    assertNotNull(result);
    System.out.println(result);
    assertEquals(result.getOrderedMenus().size(), 2);
    assertEquals(menu1.getStock(), 10 - 3);
    assertEquals(menu2.getStock(), 10 - 3);
    assertEquals(order.getOrderStatus(), OrderStatus.PaySuccess);
    assertEquals(result.getTotalQuantity(), 6);
    assertEquals(result.getTotalPrice(), cartMenu1.getMenus().getPrice() * cartMenu1.getQuantity() + cartMenu2.getMenus().getPrice() * cartMenu2.getQuantity());
//    assertEquals(cartRepository.findById(1L).get().getCartMenu().size(), 0);
    assertEquals(cartMenusRepository.findAllByCartId(1L).size() , 0);
  }
}