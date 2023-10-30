package com.chs.cafeapp.cart.controller;

import com.chs.cafeapp.cart.dto.CartInput;
import com.chs.cafeapp.cart.dto.CartMenuDto;
import com.chs.cafeapp.cart.dto.CartMenuChangeQuantity;
import com.chs.cafeapp.cart.dto.CartResponse;
import com.chs.cafeapp.cart.service.CartMenuService;
import com.chs.cafeapp.cart.service.CartService;
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
import com.chs.cafeapp.exception.CustomException;

/**
 * 장바구니 Controller
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/carts")
public class CartController {
  private final CartService cartService;
  private final CartMenuService cartMenuService;

  /**
   * 장바구니 수량 변경 (수정 예정)
   */
  @PostMapping()
  public CartResponse addCart(@RequestBody CartInput request, @RequestParam String userId) {
    CartMenuDto cartMenuDto = cartService.addCart(request, userId);
    return CartResponse.toResponse(cartMenuDto, "해당 메뉴가 장바구니에 추가되었습니다.");
  }

  /**
   * 장바구니 조회
   * @param userId
   * @return 해당 사용자의 장바구니에 등록된 CartMenu List 형식으로 반환, 장바구니가 비어있을 경우 빈 List 반환
   * @throws CustomException: 해당 userId를 가진 사용자가 없을 경우 CustomException 발생
   */
  @GetMapping()
  public ResponseEntity<List<CartMenuDto>> viewCart(@RequestParam String userId) {
    List<CartMenuDto> cartMenuDtos = cartService.viewAllCartMenuInCart(userId);
    return ResponseEntity.ok(cartMenuDtos);
  }

  /**
   * 장바구니 전체 삭제
   * @param cartId: 삭제할 장바구니 id
   * @param userId: 삭제할 장바구니를 가진 사용자 loginId
   * @return String: "해당 장바구니를 전체 삭제 했습니다."
   * @throws CustomException: 장바구니의 실제 사용자와 파라미터 값으로 찾은 사용자가 다를 경우,
   *                          장바구니와 사용자가 없을 경우, 이미 비어있는 경우 CustomException 발생
   */
  @DeleteMapping("/{cartId}")
  public ResponseEntity<String> deleteAll(@PathVariable Long cartId, @RequestParam String userId) {
    cartMenuService.deleteAllCartMenu(cartId, userId);
    return ResponseEntity.ok("해당 장바구니를 전체 삭제 했습니다.");
  }

  /**
   * 장바구니 특정 메뉴 삭제
   * @param cartMenuId: 삭제할 장바구니 메뉴 id
   * @param userId: 해당 장바구니를 가진 사용자 loginId
   * @return String: "해당 선택한 메뉴가 장바구니에서 삭제됐습니다."
   * @throws CustomException: 장바구니의 실제 사용자와 파라미터 값으로 찾은 사용자가 다를 경우,
   *                          장바구니와 사용자가 없을 경우, 장바구니가 비어있는 경우,
   *                          해당 장바구니 메뉴가 없는 경우 CustomException 발생
   */
  @DeleteMapping("/{cartId}/cartMenus/{cartMenuId}")
  public ResponseEntity<String> deleteSpecific(@PathVariable Long cartId, @PathVariable Long cartMenuId, @RequestParam String userId) {
    cartMenuService.deleteSpecificCartMenu(cartId, cartMenuId, userId);
    return ResponseEntity.ok("해당 선택한 메뉴가 장바구니에서 삭제됐습니다.");
  }


  /**
   * 장바구니 메뉴 수량 변경
   * @param request: 장바구니 id, 장바구니 menuId, 변경할 수량(예시> 5, -10)
   * @param userId: 장바구니를 갖고 있는 사용자
   * @return CartResponse: 수량 변경된 장바구니 메뉴 정보와 message 반환
   * @throws CustomException: 사용자 loginId가 존재하지 않는 경우, 장바구니가 존재하지 않는 경우,
   *                          장바구니에 해당 메뉴가 존재하지 않을 경우, 사용자의 장바구니 id 가 아닐경우,
   *                          CustomException 발생
   */
  @PatchMapping("/change")
  public ResponseEntity<CartResponse> changeCartMenuQuantity(@RequestBody CartMenuChangeQuantity request, @RequestParam String userId) {
    CartMenuDto cartMenuDto = cartMenuService.changeCartMenuQuantity(request, userId);
    String message = "";
    if(request.getQuantity() > 0) {
      message = "장바구니의 해당 메뉴의 수량이 증가되었습니다.";
    } else if (request.getQuantity() < 0) {
      message = "장바구니의 해당 메뉴의 수량이 감소되었습니다.";
    }
    return ResponseEntity.ok(CartResponse.toResponse(cartMenuDto, message));
  }

}
