package com.chs.cafeapp.service.order;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
import com.chs.cafeapp.order.dto.OrderFromCartInput;
import com.chs.cafeapp.order.dto.OrderInput;
import com.chs.cafeapp.order.entity.Order;
import com.chs.cafeapp.order.entity.OrderedMenu;
import com.chs.cafeapp.order.repository.OrderRepository;
import com.chs.cafeapp.order.repository.OrderedMenuRepository;
import com.chs.cafeapp.order.service.impl.OrderServiceImpl;
import com.chs.cafeapp.order.type.OrderStatus;
import com.chs.cafeapp.user.entity.User;
import com.chs.cafeapp.user.repository.UserRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

//    userRepository.save(user);
//    menuRepository.save(menu1);
//    menuRepository.save(menu2);
//    cartRepository.save(cart);
//    cartMenusRepository.save(cartMenu1);
//    cartMenusRepository.save(cartMenu2);

    when(userRepository.findByLoginId("user2@naver.com")).thenReturn(Optional.of(user));
    when(cartRepository.findById(1L)).thenReturn(Optional.of(cart)).thenReturn(any());
    when(menuRepository.findById(1L)).thenReturn(Optional.of(menu1));
    when(menuRepository.findById(2L)).thenReturn(Optional.of(menu2));
    when(orderRepository.save(any(Order.class))).thenReturn(order);

    OrderedMenu orderedMenu1 = OrderedMenu.builder()
        .userId(cartMenu1.getCart().getUser().getLoginId())
        .quantity(cartMenu1.getQuantity())
        .totalPrice(cartMenu1.getQuantity() * cartMenu1.getMenus().getPrice())
        .menus(cartMenu1.getMenus())
        .order(order)
        .build();
    OrderedMenu orderedMenu2 = OrderedMenu.builder()
        .userId(cartMenu2.getCart().getUser().getLoginId())
        .quantity(cartMenu2.getQuantity())
        .totalPrice(cartMenu2.getQuantity() * cartMenu2.getMenus().getPrice())
        .menus(cartMenu2.getMenus())
        .order(order)
        .build();

    System.out.println(order.getOrderStatus());

    // orderedMenuRepository를 Mock 객체로 생성합니다.
    when(orderedMenuRepository.save(any(OrderedMenu.class)))
        .thenReturn(orderedMenu1) // 첫 번째 호출에 대한 반환 값
        .thenReturn(orderedMenu2); // 두 번째 호출에 대한 반환 값

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

    // 해당 메서드 진행시 cart, cartMenu도 삭제된 것을 확인하고 싶으나 mock이 되어있어서 그대로 2가 반환됨.
//    assertEquals(cartRepository.findById(1L).get().getCartMenu().size(), 0);
//    assertEquals(cartMenusRepository.findAll().size() , 0);
  }

  @Test
  @Transactional
  @DisplayName("장바구니 선택 메뉴 주문 성공")
  void orderFromCart_Success() {
      //given
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

    Menus menu3 = Menus.builder()
        .id(3L)
        .name("따뜻한 핫 아메리카노")
        .kcal(8)
        .description("고소한 원두가 특징인 핫 카페 아메리카노 입니다.")
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

    CartMenu cartMenu3 = CartMenu.builder()
        .id(3L)
        .menus(menu3)
        .quantity(5)
        .build();

    Cart cart = Cart.builder()
        .id(1L)
        .cartMenu(Arrays.asList(cartMenu1, cartMenu2, cartMenu3))
        .user(user)
        .build();

    Order order = Order.builder()
        .id(1L)
        .user(user)
        .build();

    cartMenu1.setCart(cart);
    cartMenu2.setCart(cart);
    cartMenu3.setCart(cart);
    user.setCart(cart);

    when(userRepository.findByLoginId("user2@naver.com")).thenReturn(Optional.of(user));
    when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
    when(cartMenusRepository.findById(1L)).thenReturn(Optional.of(cartMenu1));
    when(cartMenusRepository.findById(3L)).thenReturn(Optional.of(cartMenu3));
    when(menuRepository.findById(1L)).thenReturn(Optional.of(menu1));
    when(menuRepository.findById(3L)).thenReturn(Optional.of(menu3));
    when(orderRepository.save(any(Order.class))).thenReturn(order);

    OrderedMenu orderedMenu1 = OrderedMenu.builder()
        .userId(cartMenu1.getCart().getUser().getLoginId())
        .quantity(cartMenu1.getQuantity())
        .totalPrice(cartMenu1.getQuantity() * cartMenu1.getMenus().getPrice())
        .menus(cartMenu1.getMenus())
        .order(order)
        .build();

    OrderedMenu orderedMenu2 = OrderedMenu.builder()
        .userId(cartMenu3.getCart().getUser().getLoginId())
        .quantity(cartMenu3.getQuantity())
        .totalPrice(cartMenu3.getQuantity() * cartMenu3.getMenus().getPrice())
        .menus(cartMenu3.getMenus())
        .order(order)
        .build();

    System.out.println(order.getOrderStatus());

    when(orderedMenuRepository.save(any(OrderedMenu.class)))
        .thenReturn(orderedMenu1)
        .thenReturn(orderedMenu2);

    // when
    OrderFromCartInput orderFromCartInput = new OrderFromCartInput(1L, Arrays.asList(1L, 3L), false);
    OrderDto result = orderService.orderFromCart(orderFromCartInput, "user2@naver.com");

    // then
    verify(cartMenuService, times(1)).deleteSpecificCartMenu(1L, 1L, "user2@naver.com");
    verify(cartMenuService, times(1)).deleteSpecificCartMenu(1L, 3L, "user2@naver.com");
    assertNotNull(result);
    System.out.println(result);
    assertEquals(result.getOrderedMenus().size(), 2);
    assertEquals(menu1.getStock(), 10 - 3);
    assertEquals(menu2.getStock(), 10);
    assertEquals(menu3.getStock(), 10 - 5);
    assertEquals(order.getOrderStatus(), OrderStatus.PaySuccess);
    assertEquals(result.getTotalQuantity(), 8);
    assertEquals(result.getTotalPrice(), cartMenu1.getMenus().getPrice() * cartMenu1.getQuantity()
                                                +cartMenu3.getMenus().getPrice() * cartMenu3.getQuantity());
  }

  @Test
  @Transactional
  @DisplayName("개별 메뉴 주문 성공")
  void orderIndividualMenu_Success() {
    //given
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

    Order order = Order.builder()
        .id(1L)
        .user(user)
        .build();

    OrderedMenu orderedMenu1 = OrderedMenu.builder()
        .userId(user.getLoginId())
        .quantity(3)
        .totalPrice(menu1.getPrice() * 3)
        .menus(menu1)
        .order(order)
        .build();

    order.setTotalQuantity(Arrays.asList(orderedMenu1));
    order.setTotalPrice(Arrays.asList(orderedMenu1));

    when(userRepository.findByLoginId("user2@naver.com")).thenReturn(Optional.of(user));
    when(menuRepository.findById(1L)).thenReturn(Optional.of(menu1));
    when(orderRepository.save(any(Order.class))).thenReturn(order);
    when(orderedMenuRepository.save(any(OrderedMenu.class))).thenReturn(orderedMenu1);

    // when
    OrderInput orderInput = new OrderInput(1L, "맛있는 닭가슴살 샌드위치", 10000, 3, false);
    OrderDto result = orderService.orderIndividualMenu(orderInput, "user2@naver.com");

    // then
    assertNotNull(result);
    System.out.println(result);
    assertEquals(result.getOrderedMenus().size(), 1);
//    assertEquals(result.getOrderedMenus(), Arrays.asList(orderedMenu1));
    assertEquals(result.getId(), 1L);
    assertEquals(menu1.getStock(), 10 - 3);
    assertEquals(menu2.getStock(), 10);
    assertEquals(order.getOrderStatus(), OrderStatus.PaySuccess);
    assertEquals(result.getTotalQuantity(), 3);
    assertEquals(result.getTotalPrice(), menu1.getPrice() * orderInput.getQuantity());

  }
}