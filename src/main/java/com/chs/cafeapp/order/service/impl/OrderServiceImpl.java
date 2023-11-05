package com.chs.cafeapp.order.service.impl;

import static com.chs.cafeapp.exception.type.ErrorCode.ALREADY_EXPIRED_COUPON;
import static com.chs.cafeapp.exception.type.ErrorCode.ALREADY_PICKUP_SUCCESS;
import static com.chs.cafeapp.exception.type.ErrorCode.ALREADY_USED_COUPON;
import static com.chs.cafeapp.exception.type.ErrorCode.CAN_NOT_ORDER_THAN_STOCK;
import static com.chs.cafeapp.exception.type.ErrorCode.CART_MENU_NOT_FOUND;
import static com.chs.cafeapp.exception.type.ErrorCode.CART_NOT_FOUND;
import static com.chs.cafeapp.exception.type.ErrorCode.COUPON_NOT_FOUND;
import static com.chs.cafeapp.exception.type.ErrorCode.EMPTY_SELECTED_CART_MENU;
import static com.chs.cafeapp.exception.type.ErrorCode.INVALID_REQUEST;
import static com.chs.cafeapp.exception.type.ErrorCode.MENU_NOT_FOUND;
import static com.chs.cafeapp.exception.type.ErrorCode.ORDER_NOT_FOUND;
import static com.chs.cafeapp.exception.type.ErrorCode.USER_NOT_FOUND;

import com.chs.cafeapp.cart.entity.Cart;
import com.chs.cafeapp.cart.entity.CartMenu;
import com.chs.cafeapp.cart.repository.CartMenusRepository;
import com.chs.cafeapp.cart.repository.CartRepository;
import com.chs.cafeapp.cart.service.CartMenuService;
import com.chs.cafeapp.coupon.entity.Coupon;
import com.chs.cafeapp.coupon.repository.CouponRepository;
import com.chs.cafeapp.exception.CustomException;
import com.chs.cafeapp.menu.entity.Menus;
import com.chs.cafeapp.menu.repository.MenuRepository;
import com.chs.cafeapp.order.dto.OrderAllFromCartInput;
import com.chs.cafeapp.order.dto.OrderDto;
import com.chs.cafeapp.order.dto.OrderFromCartInput;
import com.chs.cafeapp.order.dto.OrderInput;
import com.chs.cafeapp.order.entity.Order;
import com.chs.cafeapp.order.entity.OrderedMenu;
import com.chs.cafeapp.order.repository.OrderRepository;
import com.chs.cafeapp.order.repository.OrderedMenuRepository;
import com.chs.cafeapp.order.service.OrderService;
import com.chs.cafeapp.order.type.OrderStatus;
import com.chs.cafeapp.stamp.service.StampService;
import com.chs.cafeapp.user.entity.User;
import com.chs.cafeapp.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
  private final MenuRepository menuRepository;
  private final UserRepository userRepository;
  private final CartRepository cartRepository;
  private final OrderRepository orderRepository;
  private final CouponRepository couponRepository;
  private final CartMenusRepository cartMenusRepository;
  private final OrderedMenuRepository orderedMenuRepository;

  private final CartMenuService cartMenuService;
  private final StampService stampService;

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

  public User validationUser(String userId) {
    User user = userRepository.findByLoginId(userId)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
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

  public CartMenu validtaionCartMenu(long cartMenuId) {
    CartMenu cartMenu = cartMenusRepository.findById(cartMenuId)
        .orElseThrow(() -> new CustomException(CART_MENU_NOT_FOUND));

    return cartMenu;
  }

  @Override
  @Transactional
  public OrderDto orderIndividualMenu(OrderInput orderInput, String userId) {

    User user = validationUser(userId);
    Menus menus = validationMenus(orderInput.getMenuId());

    OrderedMenu saveOrderedMenu = orderedMenuRepository.save(
                                          OrderedMenu.builder()
                                                      .userId(userId)
                                                      .quantity(orderInput.getQuantity())
                                                      .totalPrice(orderInput.getMenuPrice() * orderInput.getQuantity())
                                                      .menus(menus)
                                                      .build());

    Order buildOrder = Order.builder()
                      .couponUse(orderInput.isCouponUse())
                      .user(user)
                      .totalQuantity(saveOrderedMenu.getQuantity())
                      .totalPrice(saveOrderedMenu.getTotalPrice())
                      .build();

    Order order = orderRepository.save(buildOrder);

    menus.minusStock(orderInput.getQuantity());
    if(menus.getStock() == 0) {
      menus.setSoldOut(true);
    }
    menuRepository.save(menus);

    order.setOrderStatus(OrderStatus.PaySuccess);
    if (orderInput.isCouponUse()) {
      Long couponId = orderInput.getCouponId();
      Coupon coupon = validationCoupon(couponId);
      int discountPrice = coupon.getPrice();
      order.minusTotalPriceByCouponUse(discountPrice);
      coupon.setUsedYn(true);
      couponRepository.save(coupon);
      order.setCouponId(couponId);
    }
    Order saveOrder = orderRepository.save(order);
    saveOrderedMenu.setOrder(saveOrder);
    orderedMenuRepository.save(saveOrderedMenu);
    return OrderDto.of(saveOrder);
  }

  @Override
  @Transactional
  public OrderDto orderFromCart(OrderFromCartInput orderFromCartInput, String userId) {

    User user = validationUser(userId);
    Cart cart = validationCart(orderFromCartInput.getCartId());

    if (orderFromCartInput.getIdList() == null) {
      throw new CustomException(EMPTY_SELECTED_CART_MENU);
    }

    if (orderFromCartInput.getIdList().size() == 1) {
      CartMenu cartMenu = validtaionCartMenu(orderFromCartInput.getIdList().get(0));
      Menus menus = validationMenus(cartMenu.getMenus().getId());

      OrderInput orderInput = OrderInput.builder()
          .menuId(menus.getId())
          .menuName(menus.getName())
          .menuPrice(menus.getPrice())
          .quantity(cartMenu.getQuantity())
          .couponUse(orderFromCartInput.isCouponUse())
          .build();

      if (orderInput.isCouponUse() && orderFromCartInput.getCouponId() != null) {
        orderInput.setCouponId(orderFromCartInput.getCouponId());
      }

      if (orderInput.isCouponUse() && orderFromCartInput.getCouponId() == null) {
        throw new CustomException(INVALID_REQUEST);
      }

      cartMenuService.deleteSpecificCartMenu(cart.getId(), cartMenu.getId(), userId);

      return orderIndividualMenu(orderInput, userId);
    }

    Order buildOrder = Order.builder()
                        .user(user)
                        .couponUse(orderFromCartInput.isCouponUse())
                        .build();

    Order order = orderRepository.save(buildOrder);
    Map<Menus, Integer> menusMap = new HashMap<>();

    for (Long cartMenuId : orderFromCartInput.getIdList()) {
      CartMenu cartMenu = cartMenusRepository.findById(cartMenuId)
          .orElseThrow(() -> new CustomException(CART_MENU_NOT_FOUND));

      Menus menus = validationMenus(cartMenu.getMenus().getId());

      if (menus.getStock() - cartMenu.getQuantity() < 0) {
        throw new CustomException(CAN_NOT_ORDER_THAN_STOCK);
      }

      menusMap.put(menus, cartMenu.getQuantity());

      OrderedMenu orderedMenu = OrderedMenu.fromCartMenu(cartMenu, order);
      OrderedMenu saveOrderedMenu = orderedMenuRepository.save(orderedMenu);
      order.getOrderedMenus().add(saveOrderedMenu);
      cartMenuService.deleteSpecificCartMenu(cart.getId(), cartMenu.getId(), userId);
    }

    for (Menus menus : menusMap.keySet()) {
      menus.minusStock(menusMap.get(menus));
      if(menus.getStock() == 0) {
        menus.setSoldOut(true);
      }
      menuRepository.save(menus);
    }

    order.setTotalPrice(order.getOrderedMenus());
    order.setTotalQuantity(order.getOrderedMenus());
    order.setOrderStatus(OrderStatus.PaySuccess);

    if (orderFromCartInput.isCouponUse()) {
      Long couponId = orderFromCartInput.getCouponId();
      Coupon coupon = validationCoupon(couponId);
      int discountPrice = coupon.getPrice();
      order.minusTotalPriceByCouponUse(discountPrice);
      coupon.setUsedYn(true);
      couponRepository.save(coupon);
      order.setCouponId(couponId);
    }
    return OrderDto.of(orderRepository.save(order));

  }

  @Override
  @Transactional
  public OrderDto orderAllFromCart(OrderAllFromCartInput orderAllFromCartInput, String userId) {
    User user = validationUser(userId);
    Cart cart = validationCart(orderAllFromCartInput.getCartId());

    Order buildOrder = Order.builder()
        .user(user)
        .couponUse(orderAllFromCartInput.isCouponUse())
        .build();

    Order order = orderRepository.save(buildOrder);

    Map<Menus, Integer> menusMap = new HashMap<>();
    for (CartMenu cartMenu : cart.getCartMenu()) {

      Menus menus = menuRepository.findById(cartMenu.getMenus().getId())
          .orElseThrow(() -> new CustomException(MENU_NOT_FOUND));

      if (menus.getStock() - cartMenu.getQuantity() < 0) {
        throw new CustomException(CAN_NOT_ORDER_THAN_STOCK);
      }

      menusMap.put(menus, cartMenu.getQuantity());

      OrderedMenu orderedMenu = OrderedMenu.fromCartMenu(cartMenu, order);
      OrderedMenu saveOrderedMenu = orderedMenuRepository.save(orderedMenu);
      order.getOrderedMenus().add(saveOrderedMenu);
    }

    cartMenuService.deleteAllCartMenu(orderAllFromCartInput.getCartId(), userId);

    order.setTotalPrice(order.getOrderedMenus());
    order.setTotalQuantity(order.getOrderedMenus());
    order.setOrderStatus(OrderStatus.PaySuccess);

    for (Menus menus : menusMap.keySet()) {
      menus.minusStock(menusMap.get(menus));
      if(menus.getStock() == 0) {
        menus.setSoldOut(true);
      }
      menuRepository.save(menus);
    }

    if (orderAllFromCartInput.isCouponUse()) {
      Long couponId = orderAllFromCartInput.getCouponId();
      Coupon coupon = validationCoupon(couponId);
      int discountPrice = coupon.getPrice();
      order.minusTotalPriceByCouponUse(discountPrice);
      coupon.setUsedYn(true);
      couponRepository.save(coupon);
      order.setCouponId(couponId);
    }
    return OrderDto.of(orderRepository.save(order));
  }

  @Override
  public OrderDto rejectOrder(long orderId) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new CustomException(ORDER_NOT_FOUND));

    if (!order.getOrderStatus().equals(OrderStatus.PaySuccess)) {
      throw new IllegalArgumentException();
    }

    order.setOrderStatus(OrderStatus.CancelByCafe);
    if (order.isCouponUse()) {
      long couponId = order.getCouponId();
      Coupon coupon = couponRepository.findById(couponId)
          .orElseThrow(() -> new CustomException(COUPON_NOT_FOUND));
      coupon.setUsedYn(false);
      couponRepository.save(coupon);
    }
    List<OrderedMenu> orderedMenus = order.getOrderedMenus();

    for (OrderedMenu orderedMenu : orderedMenus) {
      Menus menus = orderedMenu.getMenus();
      menus.plusStockByCancel(orderedMenu.getQuantity());
      menuRepository.save(menus);
    }
    // TODO: 소비자가 결제한 금액 환불 로직

    // TODO: orderMenu만 삭제할지, order도 삭제할지 고려,

    return OrderDto.of(orderRepository.save(order));
  }

  public Order validationChangeOrderStatus(long orderId) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new CustomException(ORDER_NOT_FOUND));

    if (order.getOrderStatus().equals(OrderStatus.PayFail)) {
      throw new IllegalStateException();
    }

    if (order.getOrderStatus().equals(OrderStatus.PickUpSuccess)) {
      throw new CustomException(ALREADY_PICKUP_SUCCESS);
    }
    return order;
  }
  @Override
  public OrderDto changeOrderStatus(long orderId) {

    Order order = validationChangeOrderStatus(orderId);

    String orderStatusName = order.getOrderStatus().name();
    switch (orderStatusName) {
      case "PaySuccess":
        order.setOrderStatus(OrderStatus.PreParingMenus);
        break;
      case "PreParingMenus":
        order.setOrderStatus(OrderStatus.WaitingPickUp);
        break;
      case "WaitingPickUp":
        order.setOrderStatus(OrderStatus.PickUpSuccess);
        long drinksCnt = order.getOrderedMenus().stream()
            .filter(orderedMenu -> "음료".equals(orderedMenu.getMenus().getCategory().getSuperCategory()))
            .mapToLong(orderedMenu -> orderedMenu.getQuantity())
            .sum();
        stampService.addStampNumbers(drinksCnt, order.getUser().getLoginId());
        break;
    }
    return OrderDto.of(orderRepository.save(order));
  }

  @Override
  public String viewMessageChanges(long orderId) {
    Order order = validationChangeOrderStatus(orderId);
    OrderStatus currOrderStatus = order.getOrderStatus();
    OrderStatus beforeOrderStatus = OrderStatus.findByNum(currOrderStatus.getNum() - 1);
    String message = beforeOrderStatus.getStatusName() + " 상태에서 ";
    message += currOrderStatus.getStatusName() + " 상태로 변경 되었습니다.";
    return message;
  }

  @Override
  public List<OrderDto> viewAllOrders() {
    return OrderDto.of(orderRepository.findAll());
  }

  @Override
  public List<OrderDto> viewOrdersByOrderStatus(int orderStatusNum) {
    OrderStatus byNumOrderStatus = OrderStatus.findByNum(orderStatusNum);
    return OrderDto.of(orderRepository.findAllByOrderStatus(byNumOrderStatus));
  }
}
