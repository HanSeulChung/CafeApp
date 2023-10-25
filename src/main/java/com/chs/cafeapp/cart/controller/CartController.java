package com.chs.cafeapp.cart.controller;

import com.chs.cafeapp.cart.dto.CartInput;
import com.chs.cafeapp.cart.dto.CartMenuDto;
import com.chs.cafeapp.cart.dto.CartMenuQuantityAdd;
import com.chs.cafeapp.cart.dto.CartMenuQuantityMinus;
import com.chs.cafeapp.cart.dto.CartResponse;
import com.chs.cafeapp.cart.service.CartMenuService;
import com.chs.cafeapp.cart.service.CartService;
import io.swagger.models.Response;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
  private final CartMenuService cartMenuService;

  /**
   * 장바구니 추가
   */
  @PostMapping()
  public CartResponse addCart(@RequestBody CartInput request, @RequestParam String userId) {
    CartMenuDto cartMenuDto = cartService.addCart(request, userId);
    return CartResponse.toResponse(cartMenuDto, "해당 메뉴가 장바구니에 추가되었습니다.");
  }

  /**
   * 장바구니 조회
   */
  @GetMapping()
  public ResponseEntity<List<CartMenuDto>> addCart(@RequestParam String userId) {
    List<CartMenuDto> cartMenuDtos = cartService.viewAllCartMenuInCart(userId);
    return ResponseEntity.ok(cartMenuDtos);
  }

  /**
   * 장바구니 전체 삭제
   */
  @DeleteMapping("/{cartId}/all")
  public ResponseEntity<String> deleteAll(@PathVariable Long cartId, @RequestParam String userId) {
    cartMenuService.deleteAllCartMenu(cartId, userId);
    return ResponseEntity.ok("해당 장바구니를 전체 삭제 했습니다.");
  }

  /**
   * 장바구니 특정 메뉴 삭제
   */
  @DeleteMapping("/{cartMenuId}")
  public ResponseEntity<String> deleteSpecific(@PathVariable Long cartMenuId, @RequestParam String userId) {
    cartMenuService.deleteSpecificCartMenu(cartMenuId, userId);
    return ResponseEntity.ok("해당 선택한 메뉴가 장바구니에서 삭제됐습니다.");
  }

  /**
   * 장바구니 메뉴 수량 증가
   */
  @PatchMapping("/change/add")
  public ResponseEntity<CartResponse> addCartMenuQuantity(@RequestBody CartMenuQuantityAdd request, @RequestParam String userId) {
    CartMenuDto cartMenuDto = cartMenuService.addCartMenuQuantity(request, userId);
    return ResponseEntity.ok(CartResponse.toResponse(cartMenuDto, "장바구니의 해당 메뉴의 수량이 증가되었습니다."));
  }

  /**
   * 장바구니 메뉴 수량 감소
   */
  @PatchMapping("/change/minus")
  public ResponseEntity<CartResponse> minusCartMenuQuantity(@RequestBody CartMenuQuantityMinus request, @RequestParam String userId) {
    CartMenuDto cartMenuDto = cartMenuService.minusCartMenuQuantity(request, userId);
    return ResponseEntity.ok(CartResponse.toResponse(cartMenuDto, "장바구니의 해당 메뉴의 수량이 감소되었습니다."));
  }
}
