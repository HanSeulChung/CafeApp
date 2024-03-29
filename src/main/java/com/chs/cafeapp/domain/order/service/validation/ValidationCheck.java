package com.chs.cafeapp.domain.order.service.validation;

import static com.chs.cafeapp.global.exception.type.ErrorCode.ALREADY_CANCEL_BY_CAFE;
import static com.chs.cafeapp.global.exception.type.ErrorCode.ALREADY_CANCEL_BY_MEMBER;
import static com.chs.cafeapp.global.exception.type.ErrorCode.ALREADY_EXPIRED_COUPON;
import static com.chs.cafeapp.global.exception.type.ErrorCode.ALREADY_USED_COUPON;
import static com.chs.cafeapp.global.exception.type.ErrorCode.CAN_NOT_ORDER_CANCEL;
import static com.chs.cafeapp.global.exception.type.ErrorCode.CART_MENU_NOT_FOUND;
import static com.chs.cafeapp.global.exception.type.ErrorCode.CART_NOT_FOUND;
import static com.chs.cafeapp.global.exception.type.ErrorCode.COUPON_NOT_FOUND;
import static com.chs.cafeapp.global.exception.type.ErrorCode.MENU_NOT_FOUND;
import static com.chs.cafeapp.global.exception.type.ErrorCode.NOT_MATCH_MEMBER_AND_ORDER;
import static com.chs.cafeapp.global.exception.type.ErrorCode.ORDER_NOT_FOUND;
import static com.chs.cafeapp.global.exception.type.ErrorCode.MEMBER_NOT_FOUND;

import com.chs.cafeapp.domain.cart.entity.Cart;
import com.chs.cafeapp.domain.cart.entity.CartMenu;
import com.chs.cafeapp.domain.cart.repository.CartMenusRepository;
import com.chs.cafeapp.domain.cart.repository.CartRepository;
import com.chs.cafeapp.domain.coupon.entity.Coupon;
import com.chs.cafeapp.domain.coupon.repository.CouponRepository;
import com.chs.cafeapp.domain.menu.entity.Menus;
import com.chs.cafeapp.domain.menu.repository.MenuRepository;
import com.chs.cafeapp.domain.order.entity.Order;
import com.chs.cafeapp.domain.order.repository.OrderRepository;
import com.chs.cafeapp.domain.order.repository.OrderedMenuRepository;
import com.chs.cafeapp.domain.order.type.OrderStatus;
import com.chs.cafeapp.global.exception.CustomException;
import com.chs.cafeapp.auth.member.entity.Member;
import com.chs.cafeapp.auth.member.repository.MemberRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ValidationCheck {
  private final MenuRepository menuRepository;
  private final MemberRepository userRepository;
  private final CartRepository cartRepository;
  private final OrderRepository orderRepository;
  private final CouponRepository couponRepository;
  private final CartMenusRepository cartMenusRepository;
  private final OrderedMenuRepository orderedMenuRepository;

  public Coupon validationCoupon(long couponId) {
    Coupon coupon = couponRepository.findById(couponId)
        .orElseThrow(() -> new CustomException(COUPON_NOT_FOUND));

    if (coupon.getExpirationDateTime().isBefore(LocalDateTime.now())) {
      throw new CustomException(ALREADY_EXPIRED_COUPON);
    }

    if (coupon.isUsedYn()) {
      throw new CustomException(ALREADY_USED_COUPON);
    }
    return coupon;
  }

  public Member validationUser(String userId) {
    Member user = userRepository.findByLoginId(userId)
        .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
    return user;
  }

  public Menus validationMenus(long menuId) {
    Menus menus = menuRepository.findById(menuId)
        .orElseThrow(() ->  new CustomException(MENU_NOT_FOUND));
    return menus;
  }

  public Cart validationCart(long cartId) {
    Cart cart = cartRepository.findById(cartId)
        .orElseThrow(() -> new CustomException(CART_NOT_FOUND));
    return cart;
  }

  public CartMenu validationCartMenu(long cartMenuId) {
    CartMenu cartMenu = cartMenusRepository.findById(cartMenuId)
        .orElseThrow(() -> new CustomException(CART_MENU_NOT_FOUND));

    return cartMenu;
  }

  public Order validationOrder(long orderId) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new CustomException(ORDER_NOT_FOUND));

    if (order.getOrderStatus().equals(OrderStatus.CancelByCafe)) {
      throw new CustomException(ALREADY_CANCEL_BY_CAFE);
    }

    if (order.getOrderStatus().equals(OrderStatus.CancelByUser)) {
      throw new CustomException(ALREADY_CANCEL_BY_MEMBER);
    }

    if (!order.getOrderStatus().equals(OrderStatus.PaySuccess)) {
      throw new CustomException(CAN_NOT_ORDER_CANCEL);
    }
    return order;
  }

  public void validationOrderAndUser(Order order, String userId) {
    Member user = userRepository.findByLoginId(userId)
        .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

    if (!order.getMember().equals(user)) {
      throw new CustomException(NOT_MATCH_MEMBER_AND_ORDER);
    }
  }
}
