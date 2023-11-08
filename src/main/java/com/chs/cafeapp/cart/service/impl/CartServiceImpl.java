package com.chs.cafeapp.cart.service.impl;

import static com.chs.cafeapp.exception.type.ErrorCode.CAN_NOT_CART_MENU_THAN_STOCK;
import static com.chs.cafeapp.exception.type.ErrorCode.MENU_NOT_FOUND;
import static com.chs.cafeapp.exception.type.ErrorCode.USER_NOT_FOUND;

import com.chs.cafeapp.cart.dto.CartInput;
import com.chs.cafeapp.cart.dto.CartMenuChangeQuantity;
import com.chs.cafeapp.cart.dto.CartMenuDto;
import com.chs.cafeapp.cart.entity.Cart;
import com.chs.cafeapp.cart.entity.CartMenu;
import com.chs.cafeapp.cart.repository.CartMenusRepository;
import com.chs.cafeapp.cart.repository.CartRepository;
import com.chs.cafeapp.cart.service.CartMenuService;
import com.chs.cafeapp.cart.service.CartService;
import com.chs.cafeapp.exception.CustomException;
import com.chs.cafeapp.menu.entity.Menus;
import com.chs.cafeapp.menu.repository.MenuRepository;
import com.chs.cafeapp.user.entity.User;
import com.chs.cafeapp.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CartServiceImpl implements CartService {

  private final UserRepository userRepository;
  private final CartRepository cartRepository;
  private final MenuRepository menuRepository;
  private final CartMenusRepository cartMenusRepository;

  private final CartMenuService cartMenuService;


  public Cart validationUserAndCart(String userId) {
    User user = userRepository.findByLoginId(userId)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    Cart cart = user.getCart();
    cart = Optional.ofNullable(cart).orElseGet(() -> {
      Cart newCart = new Cart();
      newCart.setUser(user);
      user.setCart(newCart);
      cartRepository.save(newCart);
      return newCart;
    });

    return cart;
  }

  @Override
  public CartMenuDto addCart(CartInput cartInput, String userId) {

    Cart cart = validationUserAndCart(userId);

    Menus menus = menuRepository.findById(cartInput.getMenuId())
        .orElseThrow(() -> new CustomException(MENU_NOT_FOUND));

    if (menus.getStock() - cartInput.getQuantity() < 0) {
      throw new CustomException(CAN_NOT_CART_MENU_THAN_STOCK);
    }

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
  public Page<CartMenuDto> viewAllCartMenuInCart(String userId, Pageable pageable) {
    Cart cart = validationUserAndCart(userId);

    if (cart.getCartMenu().isEmpty()) {
      return Page.empty();
    }
    Page<CartMenu> cartMenuPage = cartMenusRepository.findAllByCartId(cart.getId(), pageable);

    List<CartMenuDto> cartMenuDtoList = CartMenuDto.convertListDtoFromPageEntity(cartMenuPage);

    return new PageImpl<>(cartMenuDtoList, pageable, cartMenuPage.getTotalElements());
  }
}
