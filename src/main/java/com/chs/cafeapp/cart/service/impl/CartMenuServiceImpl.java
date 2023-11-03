package com.chs.cafeapp.cart.service.impl;

import static com.chs.cafeapp.exception.type.ErrorCode.CAN_NOT_CART_MENU_THAN_STOCK;
import static com.chs.cafeapp.exception.type.ErrorCode.CAN_NOT_MINUS_THAN_CART_MENU_QUANTITY;
import static com.chs.cafeapp.exception.type.ErrorCode.CART_MENU_NOT_FOUND;
import static com.chs.cafeapp.exception.type.ErrorCode.CART_NOT_FOUND;
import static com.chs.cafeapp.exception.type.ErrorCode.NOT_MATCH_USER_AND_CART;
import static com.chs.cafeapp.exception.type.ErrorCode.USER_NOT_FOUND;
import static com.chs.cafeapp.exception.type.ErrorCode.ZERO_CART_MENU_IN_CART;

import com.chs.cafeapp.cart.dto.CartMenuChangeQuantity;
import com.chs.cafeapp.cart.dto.CartMenuDto;
import com.chs.cafeapp.cart.entity.Cart;
import com.chs.cafeapp.cart.entity.CartMenu;
import com.chs.cafeapp.cart.repository.CartMenusRepository;
import com.chs.cafeapp.cart.repository.CartRepository;
import com.chs.cafeapp.cart.service.CartMenuService;
import com.chs.cafeapp.exception.CustomException;
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
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
    if (user.getCart().getId() != cartId) {
      throw new CustomException(NOT_MATCH_USER_AND_CART);
    }
    return user;
  }

  public Cart validationCart(Long cartId) {
    Cart cart = cartRepository.findById(cartId)
        .orElseThrow(() -> new CustomException(CART_NOT_FOUND));
    if(cart.getCartMenu().size() == 0) {
      throw new CustomException(ZERO_CART_MENU_IN_CART);
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
        .orElseThrow(() -> new CustomException(CART_MENU_NOT_FOUND));

    cart.getCartMenu().remove(cartMenu);
    cart.minusTotalPrice(cartMenu.getQuantity(), cartMenu.getMenus().getPrice());
    cart.minusTotalQuantity(cartMenu.getQuantity());
    cartRepository.save(cart);
  }

  @Override
  public CartMenuDto changeCartMenuQuantity(CartMenuChangeQuantity cartMenuChangeQuantity, String userId) {
    CartMenu cartMenu = cartMenusRepository.findById(cartMenuChangeQuantity.getCartMenuId())
        .orElseThrow(() -> new CustomException(CART_MENU_NOT_FOUND));

    User user = validationUser(cartMenu.getCart().getId(), userId);
    Cart cart = validationCart(cartMenu.getCart().getId());

    if (cartMenu.getCart().getId() != user.getCart().getId()) {
      throw new CustomException(NOT_MATCH_USER_AND_CART);
    }

    if (cartMenu.getQuantity() + cartMenuChangeQuantity.getQuantity() > cartMenu.getMenus().getStock()) {
      throw new CustomException(CAN_NOT_CART_MENU_THAN_STOCK);
    }

    if (cartMenu.getQuantity() + cartMenuChangeQuantity.getQuantity() <=0) {
      throw new CustomException(CAN_NOT_MINUS_THAN_CART_MENU_QUANTITY);
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
