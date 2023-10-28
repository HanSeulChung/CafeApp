package com.chs.cafeapp.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.chs.cafeapp.cart.entity.Cart;
import com.chs.cafeapp.cart.entity.CartMenu;
import com.chs.cafeapp.cart.repository.CartMenusRepository;
import com.chs.cafeapp.cart.repository.CartRepository;
import com.chs.cafeapp.menu.entity.Menus;
import com.chs.cafeapp.menu.repository.MenuRepository;
import com.chs.cafeapp.user.entity.User;
import com.chs.cafeapp.user.repository.UserRepository;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.yml")
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class CartandCartMenuTest {
  @Autowired
  private TestEntityManager em;

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private MenuRepository menuRepository;

  @Autowired
  private CartRepository cartRepository;

  @Autowired
  private CartMenusRepository cartMenusRepository;

  @BeforeEach
  void setUp() {
    User user = User.builder()
        .id(1L)
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

    menuRepository.save(menu1);
    menuRepository.save(menu2);
    userRepository.save(user);
    cartRepository.save(cart);

    cartMenu1.setMenus(menu1);
    cartMenu2.setMenus(menu2);
    cartMenu1.setCart(cart);
    cartMenu2.setCart(cart);
    cartMenusRepository.save(cartMenu1);
    cartMenusRepository.save(cartMenu2);
  }
  @Test
  @DisplayName("CartMenu가 삭제되면 Cart에서도 cartMenu List가 삭제되는 것 확인")
  void deleteCartMenuAndCartTest() {
    //given

    Optional<Cart> cart = cartRepository.findById(1L);


    //when

    cartMenusRepository.deleteAllByCartId(1L);

    //then
    assertEquals(cart.get().getCartMenu().size(), 0);
  }
}
