package com.chs.cafeapp.exception.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
  INTERNAL_SERVER_ERROR(500,"내부 서버 오류가 발생했습니다."),
  INVALID_REQUEST(400, "잘못된 요청입니다."),
  ALREADY_EXISTS_USER_LOGIN_ID(400, "이미 존재하는 아이디 입니다. 다른 아이디로 회원가입 진행 가능합니다."),
  ALREADY_EXISTS_USER_NICK_NAME(400, "이미 존재하는 닉네임 입니다. 다른 닉네임으로 회원가입 진행 가능합니다."),
  NOT_EXISTS_USER_LOGIN_ID(400, "로그인 아이디가 존재하지 않습니다. 다시 확인해 주십시오."),
  USER_NOT_FOUND(400, "사용자가 없습니다."),
  MENU_NOT_FOUND(400, "메뉴가 없습니다."),
  CATEGORY_NOT_FOUND(400, "카테고리가 없습니다."),
  CART_NOT_FOUND(400, "장바구니가 없습니다."),
  CART_MENU_NOT_FOUND(400, "장바구니 메뉴가 없습니다."),
  ORDER_NOT_FOUND(400, "주문이 없습니다."),
  ORDER_MENU_NOT_FOUND(400, "주문 메뉴가 없습니다."),
  COUPON_NOT_FOUND(400, "쿠폰이 없습니다."),
  EMPTY_SELECTED_CART_MENU(400, "장바구니에서 장바구니 메뉴가 선택되지 않았습니다."),
  NOT_MATCH_USER_AND_CART(400, "사용자의 장바구니가 아닙니다."),
  NOT_MATCH_USER_AND_COUPON(400, "사용자의 쿠폰이 아닙니다."),
  NOT_MATCH_USER_AND_ORDER(400, "사용자의 주문이 아닙니다."),
  ZERO_CART_MENU_IN_CART(400, "장바구니에 담긴 장바구니 메뉴가 없습니다."),
  EXIST_MENU_NAME(400, "이미 있는 메뉴 이름입니다."),
  REMAIN_CART_MENU_IN_CART(400, "장바구니에 메뉴가 남아있습니다."),
  CAN_NOT_CART_MENU_THAN_STOCK(400, "메뉴의 재고 이상 장바구니에 담을 수 없습니다."),
  CAN_NOT_ORDER_THAN_STOCK(400, "메뉴의 재고 이상 주문할 수 없습니다."),
  CAN_NOT_MINUS_THAN_STOCK(400, "메뉴의 재고보다 더 줄일 수 없습니다."),
  CAN_NOT_MINUS_THAN_CART_MENU_QUANTITY(400, "장바구니 메뉴는 0개가 될 수 없습니다. 해당 장바구니 메뉴를 없애고 싶으면 삭제해주세요."),
  CAN_NOT_ORDER_CANCEL(400, "주문을 취소할 수 없는 상태입니다."),
  ALREADY_EXPIRED_COUPON(400, "이미 기간이 만료된 쿠폰입니다."),
  ALREADY_USED_COUPON(400, "이미 사용이 완료된 쿠폰입니다."),
  ALREADY_SOLD_OUT(400, "이미 품절된 메뉴입니다."),
  ALREADY_SALE(400, "이미 판매중인 메뉴입니다."),
  ALREADY_CANCEL_BY_CAFE(400, "이미 취소된 주문입니다."),
  ALREADY_CANCEL_BY_USER(400, "이미 결제 취소된 주문입니다."),
  ALREADY_PICKUP_SUCCESS(400, "이미 픽업 완료된 주문입니다.");

  private int httpCode;
  private String description;
}
