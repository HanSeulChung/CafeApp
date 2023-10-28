package com.chs.cafeapp.service.cart;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.chs.cafeapp.cart.dto.CartInput;
import com.chs.cafeapp.cart.dto.CartMenuDto;
import com.chs.cafeapp.cart.entity.Cart;
import com.chs.cafeapp.cart.entity.CartMenu;
import com.chs.cafeapp.cart.repository.CartMenusRepository;
import com.chs.cafeapp.cart.repository.CartRepository;
import com.chs.cafeapp.cart.service.impl.CartServiceImpl;
import com.chs.cafeapp.menu.entity.Menus;
import com.chs.cafeapp.menu.repository.MenuRepository;
import com.chs.cafeapp.user.entity.User;
import com.chs.cafeapp.user.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {
  @Mock
  private UserRepository userRepository;
  @Mock
  private MenuRepository menuRepository;
  @Mock
  private CartRepository cartRepository;
  @Mock
  private CartMenusRepository cartMenusRepository;
  @InjectMocks
  private CartServiceImpl cartService; // 실제 구현된 Impl Service를 INjectMocks를 써야함

  @Test
  void addCartTest_Success() {
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

    Menus menu = Menus.builder()
                      .id(1L)
                      .name("맛있는 닭가슴살 샌드위치")
                      .kcal(500)
                      .description("특제 비법 소스를 넣어 굉장히 맛있고 건강한 샌드위치 입니다.")
                      .stock(50)
                      .price(10)
                      .isSoldOut(false)
                      .build();

    Cart cart = Cart.builder()
                    .id(1L)
                    .build();

    CartMenu cartMenu = CartMenu.builder()
                              .menus(menu)
                              .quantity(3)
                              .build();

    cartMenu.setCart(cart);
    cart.setCartMenu(cartMenu);
    user.setCart(cart);

    given(userRepository.findByLoginId(anyString()))
        .willReturn(Optional.of(user));

    given(menuRepository.findById(1L))
        .willReturn(Optional.of(menu));

    when(userRepository.findByLoginId("user2@naver.com")).thenReturn(Optional.of(user));
    when(menuRepository.findById(1L)).thenReturn(Optional.of(menu));
    when(cartMenusRepository.save(any(CartMenu.class)))
        .thenReturn(cartMenu);

    //when
    CartInput cartInput = new CartInput(1L, 3);
    CartMenuDto cartMenuDto = cartService.addCart(cartInput, "user2@naver.com");

    //then
    assertNotNull(cartMenuDto);
    assertEquals(1L, cartMenuDto.getMenuId());
    assertEquals(3, cartMenuDto.getQuantity());
    assertEquals(1L, cartMenuDto.getCartId());
    assertEquals("맛있는 닭가슴살 샌드위치", cartMenuDto.getMenuName());

    verify(userRepository).findByLoginId("user2@naver.com");
    verify(menuRepository).findById(1L);
    verify(cartMenusRepository).findByMenusId(1L);
    verify(cartMenusRepository).save(any());
    verify(cartRepository).save(any());
  }

  @Test
  void addCartTest_Fail_Stock() {
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

    Menus menu = Menus.builder()
        .id(1L)
        .name("맛있는 닭가슴살 샌드위치")
        .kcal(500)
        .description("특제 비법 소스를 넣어 굉장히 맛있고 건강한 샌드위치 입니다.")
        .stock(2)
        .price(1000)
        .isSoldOut(false)
        .build();

    Cart cart = Cart.builder()
        .id(1L)
        .build();

    CartMenu cartMenu = CartMenu.builder()
        .menus(menu)
        .quantity(3)
        .build();

    cartMenu.setCart(cart);
    cart.setCartMenu(cartMenu);
    user.setCart(cart);

    when(userRepository.findByLoginId("user2@naver.com")).thenReturn(Optional.of(user));
    when(menuRepository.findById(1L)).thenReturn(Optional.of(menu));
    //when
    CartInput cartInput = new CartInput(1L, 3);
    RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
      cartService.addCart(cartInput, "user2@naver.com");
    });

    //then
    assertEquals(runtimeException.getMessage(),  "해당 메뉴의 재고 수량만큼만 장바구니에 담을 수 있습니다.");
  }
}