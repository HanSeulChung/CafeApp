package com.chs.cafeapp.cart.controller;

import com.chs.cafeapp.cart.dto.CartInput;
import com.chs.cafeapp.cart.dto.CartMenuDto;
import com.chs.cafeapp.cart.dto.CartResponse;
import com.chs.cafeapp.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 장바구니 Controller
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {
  private final CartService cartService;

  /**
   * 장바구니 추가
   */
  @PostMapping()
  public CartResponse addCart(@RequestBody CartInput request, @RequestParam String userId) {
    CartMenuDto cartMenuDto = cartService.addOrder(request, userId);
    return CartResponse.toResponse(cartMenuDto);
  }

}
