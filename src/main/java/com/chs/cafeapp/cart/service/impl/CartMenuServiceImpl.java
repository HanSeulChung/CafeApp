package com.chs.cafeapp.cart.service.impl;

import com.chs.cafeapp.cart.entity.Cart;
import com.chs.cafeapp.cart.entity.CartMenu;
import com.chs.cafeapp.cart.repository.CartMenusRepository;
import com.chs.cafeapp.cart.repository.CartRepository;
import com.chs.cafeapp.cart.service.CartMenuService;
import com.chs.cafeapp.user.entity.User;
import com.chs.cafeapp.user.repository.UserRepository;
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
    cart.resetTotalPrice();
    cart.resetTotalQuantity();
    cartRepository.save(cart);
    cartMenusRepository.deleteAllByCartId(cartId);
  }

  @Override
  public void deleteSpecificCartMenu(Long cartMenuId, String userId) {

    User user = userRepository.findByLoginId(userId)
        .orElseThrow(() -> new RuntimeException("해당 사용자가 존재하지 않습니다."));
    Cart cart = cartRepository.findById(user.getCart().getId())
        .orElseThrow(() -> new RuntimeException("장바구니가 존재하지 않습니다."));

//    if (!user.getCart().getCartMenu().contains(cartMenuId)) {
//      throw new RuntimeException("사용자의 장바구니에 담긴 메뉴가 아닙니다.");
//    }

    if(cart.getCartMenu().size() == 0) {
      throw new RuntimeException("장바구니에 담긴 메뉴가 없습니다.");
    }

    CartMenu cartMenu = cartMenusRepository.findById(cartMenuId)
        .orElseThrow(() -> new RuntimeException("장바구니에 담겨져있는 메뉴가 아닙니다."));
    cart.minusTotalPrice(cartMenu.getQuantity(), cartMenu.getMenus().getPrice());
    cart.minusTotalQuantity(cartMenu.getQuantity());
    cartRepository.save(cart);
    cartMenusRepository.deleteById(cartMenuId);
  }
}
