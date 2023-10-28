package com.chs.cafeapp.service.cart;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.chs.cafeapp.cart.dto.CartMenuQuantityAdd;
import com.chs.cafeapp.cart.dto.CartMenuQuantityMinus;
import com.chs.cafeapp.cart.entity.Cart;
import com.chs.cafeapp.cart.entity.CartMenu;
import com.chs.cafeapp.cart.repository.CartMenusRepository;
import com.chs.cafeapp.cart.repository.CartRepository;
import com.chs.cafeapp.cart.service.impl.CartMenuServiceImpl;
import com.chs.cafeapp.menu.entity.Menus;
import com.chs.cafeapp.menu.repository.MenuRepository;
import com.chs.cafeapp.user.entity.User;
import com.chs.cafeapp.user.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CartMenuServiceImplTest {
  @Mock
  private UserRepository userRepository;

  @Mock
  private MenuRepository menuRepository;
  @Mock
  private CartRepository cartRepository;

  @Mock
  private CartMenusRepository cartMenusRepository;

  @InjectMocks
  private CartMenuServiceImpl cartMenuService;


  @Test
  @DisplayName("장바구니 전체 삭제 성공")
  void deleteAllCartMenuTest_Success() {
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
        .stock(50)
        .price(10000)
        .isSoldOut(false)
        .build();

    Menus menu2 = Menus.builder()
        .id(2L)
        .name("시원한 아이스 아메리카노")
        .kcal(8)
        .description("고소한 원두가 특징인 아이스 카페 아메리카노 입니다.")
        .stock(100)
        .price(4100)
        .isSoldOut(false)
        .build();

    Cart cart = Cart.builder()
        .id(1L)
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

    cartMenu1.setCart(cart);
    cartMenu2.setCart(cart);
    cart.setCartMenu(cartMenu1);
    cart.setCartMenu(cartMenu2);
    user.setCart(cart);

    when(userRepository.findByLoginId("user2@naver.com")).thenReturn(Optional.of(user));
    when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));

    //when
    assertDoesNotThrow(() -> cartMenuService.deleteAllCartMenu(1L, "user2@naver.com"));
    //then
    assertTrue(cart.getCartMenu().isEmpty());
  }

  @Test
  @DisplayName("장바구니 특정 메뉴 삭제 성공")
  void deleteSpecificCartMenuTest_Success() {
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
        .stock(50)
        .price(10000)
        .isSoldOut(false)
        .build();

    Menus menu2 = Menus.builder()
        .id(2L)
        .name("시원한 아이스 아메리카노")
        .kcal(8)
        .description("고소한 원두가 특징인 아이스 카페 아메리카노 입니다.")
        .stock(100)
        .price(4100)
        .isSoldOut(false)
        .build();

    Cart cart = Cart.builder()
        .id(1L)
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

    cartMenu1.setCart(cart);
    cartMenu2.setCart(cart);
    cart.setCartMenu(cartMenu1);
    cart.setCartMenu(cartMenu2);
    user.setCart(cart);

    when(userRepository.findByLoginId("user2@naver.com")).thenReturn(Optional.of(user));
    when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
    when(cartMenusRepository.findById(1L)).thenReturn(Optional.of(cartMenu1));

    //when
    assertDoesNotThrow(() -> cartMenuService.deleteSpecificCartMenu(1L, "user2@naver.com"));
    //then
    assertTrue(cart.getCartMenu().size() == 1);
    assertEquals(cart.getCartMenu().get(0), cartMenu2);
  }

  @Test
  @DisplayName("장바구니 메뉴 수량 늘리기 성공")
  void addCartMenuQuantityTest_Success() {
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
        .stock(50)
        .price(10000)
        .isSoldOut(false)
        .build();

    Cart cart = Cart.builder()
        .id(1L)
        .build();

    CartMenu cartMenu1 = CartMenu.builder()
        .id(1L)
        .menus(menu1)
        .quantity(3)
        .build();

    cartMenu1.setCart(cart);
    cart.setCartMenu(cartMenu1);
    user.setCart(cart);

    when(userRepository.findByLoginId("user2@naver.com")).thenReturn(Optional.of(user));
    when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
    when(cartMenusRepository.findById(1L)).thenReturn(Optional.of(cartMenu1));
    when(cartMenusRepository.save(cartMenu1)).thenReturn(cartMenu1);

    //when
    CartMenuQuantityAdd cartMenuQuantityAdd = new CartMenuQuantityAdd(1L, 1L, 20);
    assertDoesNotThrow(() -> cartMenuService.addCartMenuQuantity(cartMenuQuantityAdd, "user2@naver.com"));
    //then
    assertTrue(cart.getCartMenu().get(0).getQuantity() == 20 + 3);
  }

  @Test
  @DisplayName("장바구니 메뉴 수량 줄이기 성공")
  void minusCartMenuQuantityTest_Success() {
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
        .stock(50)
        .price(10000)
        .isSoldOut(false)
        .build();

    Cart cart = Cart.builder()
        .id(1L)
        .build();

    CartMenu cartMenu1 = CartMenu.builder()
        .id(1L)
        .menus(menu1)
        .quantity(15)
        .build();

    cartMenu1.setCart(cart);
    cart.setCartMenu(cartMenu1);
    user.setCart(cart);

    when(userRepository.findByLoginId("user2@naver.com")).thenReturn(Optional.of(user));
    when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
    when(cartMenusRepository.findById(1L)).thenReturn(Optional.of(cartMenu1));
    when(cartMenusRepository.save(cartMenu1)).thenReturn(cartMenu1);

    //when
    CartMenuQuantityMinus cartMenuQuantityMinus = new CartMenuQuantityMinus(1L, 1L, 5);
    assertDoesNotThrow(() -> cartMenuService.minusCartMenuQuantity(cartMenuQuantityMinus, "user2@naver.com"));
    //then
    assertTrue(cart.getCartMenu().get(0).getQuantity() == 15 - 5);
  }
}