package com.chs.cafeapp.cart.service.impl;

import com.chs.cafeapp.cart.dto.CartMenuDto;
import com.chs.cafeapp.cart.dto.CartMenuQuantityAdd;
import com.chs.cafeapp.cart.dto.CartMenuQuantityMinus;
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

  @Override
  public void deleteAllCartMenu(Long cartId, String userId) {
    User user = userRepository.findByLoginId(userId)
        .orElseThrow(() -> new RuntimeException("해당 사용자가 존재하지 않습니다."));
    Cart cart = cartRepository.findById(cartId)
        .orElseThrow(() -> new RuntimeException("장바구니가 존재하지 않습니다."));

    if (user.getCart().getId() != cartId) {
      throw new RuntimeException("사용자의 장바구니가 아닙니다.");
    }

    if(cart.getCartMenu().size() == 0) {
      throw new RuntimeException("장바구니에 담긴 메뉴가 없습니다.");
    }

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
  public void deleteSpecificCartMenu(Long cartMenuId, String userId) {

    User user = userRepository.findByLoginId(userId)
        .orElseThrow(() -> new RuntimeException("해당 사용자가 존재하지 않습니다."));
    Cart cart = cartRepository.findById(user.getCart().getId())
        .orElseThrow(() -> new RuntimeException("장바구니가 존재하지 않습니다."));

    if(cart.getCartMenu().size() == 0) {
      throw new RuntimeException("장바구니에 담긴 메뉴가 없습니다.");
    }

    CartMenu cartMenu = cartMenusRepository.findById(cartMenuId)
        .orElseThrow(() -> new RuntimeException("장바구니에 담겨져있는 메뉴가 아닙니다."));

    cart.getCartMenu().remove(cartMenu);
    cart.minusTotalPrice(cartMenu.getQuantity(), cartMenu.getMenus().getPrice());
    cart.minusTotalQuantity(cartMenu.getQuantity());
    cartRepository.save(cart);
    cartMenusRepository.deleteById(cartMenuId);
  }

  @Override
  public CartMenuDto addCartMenuQuantity(CartMenuQuantityAdd cartMenuQuantityAdd, String userId) {
    User user = userRepository.findByLoginId(userId)
        .orElseThrow(() -> new RuntimeException("해당 사용자가 존재하지 않습니다."));
    Cart cart = cartRepository.findById(user.getCart().getId())
        .orElseThrow(() -> new RuntimeException("장바구니가 존재하지 않습니다."));

    CartMenu cartMenu = cartMenusRepository.findById(cartMenuQuantityAdd.getId())
        .orElseThrow(() -> new RuntimeException("장바구니에 해당 장바구니 메뉴가 존재하지 않습니다. 다시 확인해주세요."));

    if (cartMenu.getMenus().getId() != cartMenuQuantityAdd.getMenuId()) {
      throw new RuntimeException("장바구니에 해당 메뉴가 존재하지 않습니다.");
    }

    if (cartMenu.getCart().getId() != user.getCart().getId()) {
      throw new RuntimeException("사용자의 장바구니에 담겨있지 않습니다.");
    }

    if (cartMenu.getQuantity() + cartMenuQuantityAdd.getQuantity() > cartMenu.getMenus().getStock()) {
      throw new RuntimeException("해당 메뉴의 재고 이상 장바구니에 추가할 수 없습니다.");
    }

    cart.getCartMenu().remove(cartMenu);
    cart.addTotalQuantity(cartMenuQuantityAdd.getQuantity());
    cart.addTotalPrice(cartMenuQuantityAdd.getQuantity(), cartMenu.getMenus().getPrice());
    cartMenu.addQuantity(cartMenuQuantityAdd.getQuantity());
    cart.getCartMenu().add(cartMenu);

    cartRepository.save(cart);

    return CartMenuDto.of(cartMenusRepository.save(cartMenu));
  }

  @Override
  public CartMenuDto minusCartMenuQuantity(CartMenuQuantityMinus cartMenuQuantityMinus, String userId) {
    User user = userRepository.findByLoginId(userId)
        .orElseThrow(() -> new RuntimeException("해당 사용자가 존재하지 않습니다."));
    Cart cart = cartRepository.findById(user.getCart().getId())
        .orElseThrow(() -> new RuntimeException("장바구니가 존재하지 않습니다."));
    CartMenu cartMenu = cartMenusRepository.findById(cartMenuQuantityMinus.getId())
        .orElseThrow(() -> new RuntimeException("장바구니에 해당 장바구니 메뉴가 존재하지 않습니다. 다시 확인해주세요."));

    if (cartMenu.getMenus().getId() != cartMenuQuantityMinus.getMenuId()) {
      throw new RuntimeException("장바구니에 해당 메뉴가 존재하지 않습니다.");
    }

    if (cartMenu.getCart().getId() != user.getCart().getId()) {
      throw new RuntimeException("사용자의 장바구니에 담겨있지 않습니다.");
    }

    if (cartMenu.getQuantity() - cartMenuQuantityMinus.getQuantity() <= 0) {
      throw new RuntimeException("더이상 감소할 수량이 없습니다. 해당 메뉴가 필요 없으면 삭제하십시오.");
    }

    cart.getCartMenu().remove(cartMenu);
    cart.minusTotalQuantity(cartMenuQuantityMinus.getQuantity());
    cart.minusTotalPrice(cartMenuQuantityMinus.getQuantity(), cartMenu.getMenus().getPrice());
    cartMenu.minusQuantity(cartMenuQuantityMinus.getQuantity());
    cart.getCartMenu().add(cartMenu);

    cartRepository.save(cart);

    return CartMenuDto.of(cartMenusRepository.save(cartMenu));
  }
}
