package com.chs.cafeapp.cart.service.impl;

import com.chs.cafeapp.cart.dto.CartMenuChangeQuantity;
import com.chs.cafeapp.cart.dto.CartMenuDto;
import com.chs.cafeapp.cart.entity.Cart;
import com.chs.cafeapp.cart.entity.CartMenu;
import com.chs.cafeapp.cart.repository.CartMenusRepository;
import com.chs.cafeapp.cart.repository.CartRepository;
import com.chs.cafeapp.cart.service.CartMenuService;
import com.chs.cafeapp.user.entity.User;
import com.chs.cafeapp.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartMenuServiceImpl implements CartMenuService {
  private final CartMenusRepository cartMenusRepository;
  private final UserRepository userRepository;
  private final CartRepository cartRepository;

  public User validationUser(Long cartId, String userId) {
    User user = userRepository.findByLoginId(userId)
        .orElseThrow(() -> new RuntimeException("해당 사용자가 존재하지 않습니다."));
    if (user.getCart().getId() != cartId) {
      throw new RuntimeException("사용자의 장바구니가 아닙니다.");
    }
    return user;
  }

  public Cart validationCart(Long cartId) {
    Cart cart = cartRepository.findById(cartId)
        .orElseThrow(() -> new RuntimeException("장바구니가 존재하지 않습니다."));
    if(cart.getCartMenu().size() == 0) {
      throw new RuntimeException("장바구니에 담긴 메뉴가 없습니다.");
    }
    return cart;
  }
  @Override
  public void deleteAllCartMenu(Long cartId, String userId) {

    validationUser(cartId, userId);
    Cart cart = validationCart(cartId);

    List<CartMenu> cartMenus = cartMenusRepository.findAllByCartId(cartId);
    for (CartMenu cartMenu : cartMenus) {
      cartMenu.setCart(null); // Cart 참조 제거
      cartMenusRepository.save(cartMenu); // 변경 사항 저장
    }

    cart.getCartMenu().clear();
    cart.resetTotalPrice();
    cart.resetTotalQuantity();
    cartRepository.save(cart);

  }

  @Override
  public void deleteSpecificCartMenu(Long cartId, Long cartMenuId, String userId) {

    validationUser(cartId, userId);
    Cart cart = validationCart(cartId);

    CartMenu cartMenu = cartMenusRepository.findById(cartMenuId)
        .orElseThrow(() -> new RuntimeException("장바구니에 담겨져있는 메뉴가 아닙니다."));

    cart.getCartMenu().remove(cartMenu);
    cart.minusTotalPrice(cartMenu.getQuantity(), cartMenu.getMenus().getPrice());
    cart.minusTotalQuantity(cartMenu.getQuantity());
    cartRepository.save(cart);
  }

  @Override
  public CartMenuDto changeCartMenuQuantity(CartMenuChangeQuantity cartMenuChangeQuantity, String userId) {
    CartMenu cartMenu = cartMenusRepository.findById(cartMenuChangeQuantity.getCartMenuId())
        .orElseThrow(() -> new RuntimeException("장바구니에 해당 장바구니 메뉴가 존재하지 않습니다. 다시 확인해주세요."));

    User user = validationUser(cartMenu.getCart().getId(), userId);
    Cart cart = validationCart(cartMenu.getCart().getId());

    if (cartMenu.getCart().getId() != user.getCart().getId()) {
      throw new RuntimeException("사용자의 장바구니에 담겨있지 않습니다.");
    }

    if (cartMenu.getQuantity() + cartMenuChangeQuantity.getQuantity() > cartMenu.getMenus().getStock()) {
      throw new RuntimeException("해당 메뉴의 재고 이상 장바구니에 추가할 수 없습니다.");
    }

    if (cartMenu.getQuantity() + cartMenuChangeQuantity.getQuantity() <=0) {
      throw new RuntimeException("해당 장바구니 메뉴를 없애고 싶으면 삭제를 해주세요.");
    }
    cart.getCartMenu().remove(cartMenu);
    cart.addTotalQuantity(cartMenuChangeQuantity.getQuantity());
    cart.addTotalPrice(cartMenuChangeQuantity.getQuantity(), cartMenu.getMenus().getPrice());
    cartMenu.addQuantity(cartMenuChangeQuantity.getQuantity());
    cart.getCartMenu().add(cartMenu);

    cartRepository.save(cart);

    return CartMenuDto.of(cartMenusRepository.save(cartMenu));
  }
}
