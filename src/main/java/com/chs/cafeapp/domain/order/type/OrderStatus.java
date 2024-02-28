package com.chs.cafeapp.domain.order.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {

  /**
   * 결제 실패
   */
  PayFail(1, "결제 실패", "결제가 실패하였습니다."),

  /**
   * 가게에서 주문 취소
   */
  CancelByCafe(2, "주문 취소", "가게에서 주문 취소했습니다."),

  /**
   * 사용자가 주문 취소 -> 결제 취소
   */
  CancelByUser(3, "결제 취소", "사용자가 주문 취소한 주문입니다."),


  /**
   * 결제 성공
   */
  PaySuccess(4, "결제 성공", "결제가 완료되었습니다. 카페에서 주문 확인 중입니다."),


  /**
   * 메뉴 준비중
   */
  PreParingMenus(5, "메뉴 준비 중", "카페에서 주문 확인 후 메뉴 준비 중입니다."),

  /**
   * 메뉴 준비 완료, 픽업 대기 중
   */
  WaitingPickUp(6, "픽업 대기 중","메뉴 준비가 완료되었습니다. 픽업 대기 중입니다."),

  /**
   * 픽업 완료
   */
  PickUpSuccess(7, "픽업 완료","픽업이 완료되었습니다.");

  private final int num;
  private final String statusName;
  private final String description;

  public static OrderStatus findByNum(int num){
    for (OrderStatus orderStatus : OrderStatus.values()){
      if(num == orderStatus.num){
        return orderStatus;
      }
    }
    throw new IllegalArgumentException("올바른 숫자가 없습니다.");
  }

}
