package com.chs.cafeapp.cart.service.impl;

import com.chs.cafeapp.cart.dto.CartInput;
import com.chs.cafeapp.cart.dto.CartMenuChangeQuantity;
import com.chs.cafeapp.cart.dto.CartMenuDto;
import com.chs.cafeapp.cart.entity.Cart;
import com.chs.cafeapp.cart.entity.CartMenu;
import com.chs.cafeapp.cart.repository.CartMenusRepository;
import com.chs.cafeapp.cart.repository.CartRepository;
import com.chs.cafeapp.cart.service.CartMenuService;
import com.chs.cafeapp.cart.service.CartService;
import com.chs.cafeapp.menu.entity.Menus;
import com.chs.cafeapp.menu.repository.MenuRepository;
import com.chs.cafeapp.user.entity.User;
import com.chs.cafeapp.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CartServiceImpl implements CartService {

  private final UserRepository userRepository;
  private final CartRepository cartRepository;
  private final MenuRepository menuRepository;
  private final CartMenusRepository cartMenusRepository;

  private final CartMenuService cartMenuService;

  @Override
  public CartMenuDto addCart(CartInput cartInput, String userId) {

    User user = userRepository.findByLoginId(userId)
        .orElseThrow(() -> new RuntimeException("해당 사용자가 없습니다."));
    Menus menus = menuRepository.findById(cartInput.getMenuId())
        .orElseThrow(() -> new RuntimeException("해당 메뉴가 존재하지 않습니다."));

    if (menus.getStock() - cartInput.getQuantity() < 0) {
      throw new RuntimeException("해당 메뉴의 재고 수량만큼만 장바구니에 담을 수 있습니다.");
    }

    Cart cart = user.getCart();
    cart = Optional.ofNullable(cart).orElseGet(() -> {
      Cart newCart = new Cart();
      newCart.setUser(user);
      user.setCart(newCart);
      cartRepository.save(newCart);
      return newCart;
    });

    boolean existsCartMenu = cartMenusRepository.existsByMenusId(cartInput.getMenuId());

    if (existsCartMenu) {
      CartMenu cartMenu = cartMenusRepository.findByMenusId(cartInput.getMenuId()).get();
      CartMenuDto cartMenuDto = cartMenuService.changeCartMenuQuantity(
          new CartMenuChangeQuantity(cartMenu.getId(), cartInput.getMenuId(),
              cartInput.getQuantity()), userId);
      return cartMenuDto;
    }

    CartMenu buildCartMenu = CartMenu.builder()
        .quantity(cartInput.getQuantity())
        .menus(menus)
        .cart(cart)
        .build();

    CartMenu saveCartMenu = cartMenusRepository.save(buildCartMenu);

    List<CartMenu> cartMenuList = cart.getCartMenu();
    cartMenuList = Optional.ofNullable(cartMenuList).orElseGet(() -> {
      List<CartMenu> newCartMenuList = new ArrayList<>();
      return newCartMenuList;
    });

    cartMenuList.add(saveCartMenu);
    cart.addTotalPrice(saveCartMenu.getQuantity(), saveCartMenu.getMenus().getPrice());
    cart.addTotalQuantity(saveCartMenu.getQuantity());
    cartRepository.save(cart);

    return CartMenuDto.of(saveCartMenu);
  }

  @Override
  public List<CartMenuDto> viewAllCartMenuInCart(String userId) {
    User user = userRepository.findByLoginId(userId)
        .orElseThrow(() -> new RuntimeException("해당 사용자가 존재하지 않습니다."));

    if (user.getCart() == null) {
      Cart newCart = new Cart();
      user.setCart(newCart);
      newCart.setUser(user);
      cartRepository.save(newCart);
    }

    Cart cart = cartRepository.findById(user.getCart().getId())
                                .orElse(null);

    if (cart != null) {
      List<CartMenuDto> cartMenuDtoList = CartMenuDto.of(cart.getCartMenu());
      return cartMenuDtoList;
    }

    return new ArrayList<>();
  }
}
