package com.chs.cafeapp.order.type;

public enum OrderStatus {

  /**
   * 결제 실패
   */
  PayFail,

  /**
   * 결제 성공
   */
  PaySuccess,

  /**
   * 가게에서 주문 취소
   */
  CancelByCafe,

  /**
   * 사용자가 주문 취소
   */
  CancelByUser,

  /**
   * 메뉴 준비중
   */
  PreParingMenus,

  /**
   * 메뉴 준비 완료, 픽업 대기 중
   */
  WaitingPickUp,

  /**
   * 픽업 완료
   */
  PickUpSuccess
}
