package com.chs.cafeapp.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.chs.cafeapp.cart.entity.Cart;
import com.chs.cafeapp.cart.entity.CartMenu;
import com.chs.cafeapp.cart.repository.CartMenusRepository;
import com.chs.cafeapp.cart.repository.CartRepository;
import com.chs.cafeapp.menu.entity.Menus;
import com.chs.cafeapp.menu.repository.MenuRepository;
import com.chs.cafeapp.user.entity.User;
import com.chs.cafeapp.user.repository.UserRepository;
import java.util.List;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;


@DataJpaTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CartandCartMenuTest {
  @Autowired
  private EntityManager em;
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

  }

//  @AfterEach
//  public void clearPersistenceContext() {
//    em.clear();
//  }
  @Test
  @DisplayName("CartMenu 저장")
  void saveCartMenu() {
    // given
    Menus menu1 = Menus.builder()
        .id(1L)
        .name("맛있는 닭가슴살 샌드위치")
        .kcal(500)
        .description("특제 비법 소스를 넣어 굉장히 맛있고 건강한 샌드위치 입니다.")
        .stock(10)
        .price(10000)
        .isSoldOut(false)
        .build();
    // when
    Menus save = menuRepository.save(menu1);
    // then
    assertEquals(save, menuRepository.findById(1L).get());
  }

  @Test
  @Transactional
  @DisplayName("CartMenu가 삭제되면 Cart에서도 cartMenu List가 삭제되는 것 확인")
  void deleteCartMenuAndCartTest() {
    //given
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

    Cart cart = Cart.builder()
        .id(1L)
        .user(user)
        .build();

    userRepository.save(user);
    menuRepository.save(menu1);
    menuRepository.save(menu2);

    CartMenu cartMenu1 = CartMenu.builder()
        .id(1L)
        .menus(menu1)
        .quantity(3)
        .build();

    Cart saveCart = cartRepository.save(cart);

    CartMenu cartMenu2 = CartMenu.builder()
        .id(2L)
        .menus(menu2)
        .quantity(3)
        .build();

    cartMenu1.setCart(saveCart);
    cartMenu2.setCart(saveCart);
    cart.setCartMenu(cartMenu1);
    cart.setCartMenu(cartMenu2);

    cartMenusRepository.save(cartMenu1);
    cartMenusRepository.save(cartMenu2);
    cartRepository.save(saveCart);
    em.clear();

    assertNotNull(cartMenusRepository.findAllByCartId(1L));
    assertEquals(cartRepository.findById(1L).get().getCartMenu().size(), 2);

    //when
    List<CartMenu> cartMenus = cartMenusRepository.findAllByCartId(1L);
    for (CartMenu cartMenu : cartMenus) {
      cartMenu.setCart(null); // Cart 참조 제거
      cartMenusRepository.save(cartMenu); // 변경 사항 저장
    }

    cartMenusRepository.deleteAllByCartId(1L); // CartMenu 삭제
    em.clear();

    //then
    assertEquals(cartRepository.findById(1L).get().getCartMenu().size(), 0);
    assertNull(cartMenusRepository.findByCartId(1L).orElse(null));
    assertEquals(cartRepository.findById(1L).get().getId(), 1L);
    assertEquals(cartRepository.findById(1L).get().getTotalPrice(), 0);
    assertEquals(cartRepository.findById(1L).get().getTotalPrice(), 0);
  }
}
