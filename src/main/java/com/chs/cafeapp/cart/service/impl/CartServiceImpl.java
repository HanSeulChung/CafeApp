package com.chs.cafeapp.cart.service.impl;

import com.chs.cafeapp.cart.dto.CartInput;
import com.chs.cafeapp.cart.dto.CartMenuDto;
import com.chs.cafeapp.cart.entity.Cart;
import com.chs.cafeapp.cart.entity.CartMenu;
import com.chs.cafeapp.cart.repository.CartMenusRepository;
import com.chs.cafeapp.cart.repository.CartRepository;
import com.chs.cafeapp.cart.service.CartService;
import com.chs.cafeapp.menu.entity.Menus;
import com.chs.cafeapp.menu.repository.MenuRepository;
import com.chs.cafeapp.user.entity.User;
import com.chs.cafeapp.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CartServiceImpl implements CartService {
  private final CartRepository cartRepository;
  private final CartMenusRepository cartMenusRepository;
  private final UserRepository userRepository;
  private final MenuRepository menuRepository;

  @Override
  public CartMenuDto addCart(CartInput cartInput, String userId) {

    User user = userRepository.findByLoginId(userId)
        .orElseThrow(() -> new RuntimeException("해당 사용자가 없습니다."));
    Menus menus = menuRepository.findById(cartInput.getMenuId())
        .orElseThrow(() -> new RuntimeException("해당 메뉴가 존재하지 않습니다."));

    Cart cart = user.getCart();

    if (menus.getStock() - cartInput.getQuantity() < 0) {
      throw new RuntimeException("해당 메뉴의 재고 수량만큼만 장바구니에 담을 수 있습니다.");
    }

    if (cart == null) {
      cart = new Cart();
      cart.setUser(user);
      user.setCart(cart);
    }

    CartMenu cartMenu = new CartMenu();
    Optional<CartMenu> byMenusId = cartMenusRepository.findByMenusId(cartInput.getMenuId());

    if (byMenusId.isPresent()) {
      cartMenu = byMenusId.get();
      cart.getCartMenu().remove(cartMenu);
      cart.minusTotalQuantity(cartMenu.getQuantity());
      cart.minusTotalPrice(cartMenu.getQuantity(), cartMenu.getMenus().getPrice());
      cartMenu.addQuantity( cartInput.getQuantity());

    } else {
      cartMenu.setMenus(menus);
      cartMenu.setQuantity(cartInput.getQuantity());
      cartMenu.setCart(cart);
    }

    CartMenu saveCartMenu = cartMenusRepository.save(cartMenu);

    cart.getCartMenu().add(cartMenu);

    cart.setTotalPrice(cartMenu.getQuantity(), cartMenu.getMenus().getPrice());
    cart.setTotalQuantity(cartMenu.getQuantity());

    cartRepository.save(cart);

    return CartMenuDto.of(saveCartMenu);
  }

  @Override
  public List<CartMenuDto> viewAllCartMenuInCart(String userId) {
    User user = userRepository.findByLoginId(userId)
        .orElseThrow(() -> new RuntimeException("해당 사용자가 존재하지 않습니다."));

    Cart cart = cartRepository.findById(user.getCart().getId()).orElse(null);
    if (cart != null) {
      List<CartMenuDto> cartMenuDtoList = CartMenuDto.of(cart.getCartMenu());
      return cartMenuDtoList;
    }

    return null;
  }
}
