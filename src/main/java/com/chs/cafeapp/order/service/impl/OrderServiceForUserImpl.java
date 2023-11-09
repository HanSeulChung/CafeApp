package com.chs.cafeapp.order.service.impl;

import static com.chs.cafeapp.exception.type.ErrorCode.CAN_NOT_ORDER_THAN_STOCK;
import static com.chs.cafeapp.exception.type.ErrorCode.COUPON_NOT_FOUND;
import static com.chs.cafeapp.exception.type.ErrorCode.EMPTY_SELECTED_CART_MENU;
import static com.chs.cafeapp.exception.type.ErrorCode.INVALID_REQUEST;
import static com.chs.cafeapp.order.type.OrderStatus.CancelByUser;
import static com.chs.cafeapp.order.type.OrderStatus.PaySuccess;

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
import com.chs.cafeapp.order.service.OrderServiceForUser;
import com.chs.cafeapp.order.service.validation.ValidationCheck;
import com.chs.cafeapp.user.entity.User;
import com.chs.cafeapp.user.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceForUserImpl implements OrderServiceForUser {

  private final MenuRepository menuRepository;
  private final UserRepository userRepository;
  private final CartRepository cartRepository;
  private final OrderRepository orderRepository;
  private final CouponRepository couponRepository;
  private final CartMenusRepository cartMenusRepository;
  private final OrderedMenuRepository orderedMenuRepository;

  private final CartMenuService cartMenuService;
  private final ValidationCheck validationCheck;

  @Override
  @Transactional
  public OrderDto orderIndividualMenu(OrderInput orderInput, String userId) {


    User user = validationCheck.validationUser(userId);
    Menus menus = validationCheck.validationMenus(orderInput.getMenuId());

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


    if (orderInput.isCouponUse()) {
      Long couponId = orderInput.getCouponId();
      Coupon coupon = validationCheck.validationCoupon(couponId);
      int discountPrice = coupon.getPrice();
      buildOrder.minusTotalPriceByCouponUse(discountPrice);
      coupon.setUsedYn(true);
      couponRepository.save(coupon);
      buildOrder.setCouponId(couponId);
    }

    menus.minusStock(orderInput.getQuantity());
    if(menus.getStock() == 0) {
      menus.setSoldOut(true);
    }
    menuRepository.save(menus);

    buildOrder.setOrderStatus(PaySuccess);
    buildOrder.setOrderedMenus(saveOrderedMenu);

    Order saveOrder = orderRepository.save(buildOrder);
    saveOrderedMenu.setOrder(saveOrder);
    orderedMenuRepository.save(saveOrderedMenu);
    return OrderDto.of(saveOrder);
  }

  @Override
  @Transactional
  public OrderDto orderFromCart(OrderFromCartInput orderFromCartInput, String userId) {

    User user = validationCheck.validationUser(userId);
    Cart cart = validationCheck.validationCart(orderFromCartInput.getCartId());

    if (orderFromCartInput.getIdList() == null) {
      throw new CustomException(EMPTY_SELECTED_CART_MENU);
    }

    if (orderFromCartInput.getIdList().size() == 1) {
      CartMenu cartMenu = validationCheck.validationCartMenu(orderFromCartInput.getIdList().get(0));
      Menus menus = validationCheck.validationMenus(cartMenu.getMenus().getId());

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
      CartMenu cartMenu = validationCheck.validationCartMenu(cartMenuId);

      Menus menus = validationCheck.validationMenus(cartMenu.getMenus().getId());

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
    order.setOrderStatus(PaySuccess);

    if (orderFromCartInput.isCouponUse()) {
      Long couponId = orderFromCartInput.getCouponId();
      Coupon coupon = validationCheck.validationCoupon(couponId);
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
    User user = validationCheck.validationUser(userId);
    Cart cart = validationCheck.validationCart(orderAllFromCartInput.getCartId());

    Order buildOrder = Order.builder()
        .user(user)
        .couponUse(orderAllFromCartInput.isCouponUse())
        .build();

    Order order = orderRepository.save(buildOrder);

    Map<Menus, Integer> menusMap = new HashMap<>();
    for (CartMenu cartMenu : cart.getCartMenu()) {
      long id = cartMenu.getMenus().getId();
      Menus menus = validationCheck.validationMenus(id);

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
    order.setOrderStatus(PaySuccess);

    for (Menus menus : menusMap.keySet()) {
      menus.minusStock(menusMap.get(menus));
      if(menus.getStock() == 0) {
        menus.setSoldOut(true);
      }
      menuRepository.save(menus);
    }

    if (orderAllFromCartInput.isCouponUse()) {
      Long couponId = orderAllFromCartInput.getCouponId();
      Coupon coupon = validationCheck.validationCoupon(couponId);
      int discountPrice = coupon.getPrice();
      order.minusTotalPriceByCouponUse(discountPrice);
      coupon.setUsedYn(true);
      couponRepository.save(coupon);
      order.setCouponId(couponId);
    }
    return OrderDto.of(orderRepository.save(order));
  }

  @Override
  public Slice<OrderDto> viewAllOrders(String userId, Pageable pageable) {
    User user = validationCheck.validationUser(userId);
    Slice<Order> orderSlice = orderRepository.findAllByUserId(pageable, user.getId());
    List<OrderDto> orderDtoList = OrderDto.convertListDtoFromPageEntity(orderSlice);
    return new SliceImpl<>(orderDtoList, pageable, orderSlice.hasNext());
  }
  @Override
  public OrderDto cancelOrder(long orderId, String userId) {
    Order order = validationCheck.validationOrder(orderId);
    validationCheck.validationOrderAndUser(order, userId);

    order.setOrderStatus(CancelByUser);
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

  @Override
  public Slice<OrderDto> viewAllOrdersDuringDays(String userId, Pageable pageable) {
    LocalDate nowDate = LocalDate.now();
    LocalDateTime nowDateTime = nowDate.atTime(23, 59, 59);
    User user = validationCheck.validationUser(userId);
    Slice<Order> orderSlice = orderRepository.findAllByUserIdAndCreateDateTimeBetween(
        user.getId(),
        nowDateTime.minusDays(1), nowDateTime,
        pageable);
    List<OrderDto> orderDtoList = OrderDto.convertListDtoFromPageEntity(orderSlice);
    return new SliceImpl<>(orderDtoList, pageable, orderSlice.hasNext());
  }

  @Override
  public Slice<OrderDto> viewAllOrdersDuringWeeks(String userId, Pageable pageable) {
    LocalDate nowDate = LocalDate.now();
    LocalDateTime nowDateTime = nowDate.atTime(23, 59, 59);
    User user = validationCheck.validationUser(userId);
    Slice<Order> orderSlice = orderRepository.findAllByUserIdAndCreateDateTimeBetween(
        user.getId(),
        nowDateTime.minusDays(7), nowDateTime,
        pageable);
    List<OrderDto> orderDtoList = OrderDto.convertListDtoFromPageEntity(orderSlice);
    return new SliceImpl<>(orderDtoList, pageable, orderSlice.hasNext());
  }

  @Override
  public Slice<OrderDto> viewAllOrdersDuringMonths(String userId, Pageable pageable) {
    LocalDate nowDate = LocalDate.now();
    LocalDateTime nowDateTime = nowDate.atTime(23, 59, 59);
    User user = validationCheck.validationUser(userId);
    Slice<Order> orderSlice = orderRepository.findAllByUserIdAndCreateDateTimeBetween(
        user.getId(),
        nowDateTime.minusMonths(1), nowDateTime,
        pageable);
    List<OrderDto> orderDtoList = OrderDto.convertListDtoFromPageEntity(orderSlice);
    return new SliceImpl<>(orderDtoList, pageable, orderSlice.hasNext());
  }

  @Override
  public Slice<OrderDto> viewAllOrdersDuringThreeMonths(String userId, Pageable pageable) {
    LocalDate nowDate = LocalDate.now();
    LocalDateTime nowDateTime = nowDate.atTime(23, 59, 59);
    User user = validationCheck.validationUser(userId);
    Slice<Order> orderSlice = orderRepository.findAllByUserIdAndCreateDateTimeBetween(
        user.getId(),
        nowDateTime.minusMonths(3), nowDateTime,
        pageable);
    List<OrderDto> orderDtoList = OrderDto.convertListDtoFromPageEntity(orderSlice);
    return new SliceImpl<>(orderDtoList, pageable, orderSlice.hasNext());
  }

  @Override
  public Slice<OrderDto> viewAllOrdersDuringSixMonths(String userId, Pageable pageable) {
    LocalDate nowDate = LocalDate.now();
    LocalDateTime nowDateTime = nowDate.atTime(23, 59, 59);
    User user = validationCheck.validationUser(userId);
    Slice<Order> orderSlice = orderRepository.findAllByUserIdAndCreateDateTimeBetween(
        user.getId(),
        nowDateTime.minusMonths(6), nowDateTime,
        pageable);
    List<OrderDto> orderDtoList = OrderDto.convertListDtoFromPageEntity(orderSlice);
    return new SliceImpl<>(orderDtoList, pageable, orderSlice.hasNext());
  }

  @Override
  public Slice<OrderDto> viewAllOrdersDuringYears(String userId, Pageable pageable) {
    LocalDate nowDate = LocalDate.now();
    LocalDateTime nowDateTime = nowDate.atTime(23, 59, 59);
    User user = validationCheck.validationUser(userId);
    Slice<Order> orderSlice = orderRepository.findAllByUserIdAndCreateDateTimeBetween(
        user.getId(),
        nowDateTime.minusYears(1), nowDateTime,
        pageable);
    List<OrderDto> orderDtoList = OrderDto.convertListDtoFromPageEntity(orderSlice);
    return new SliceImpl<>(orderDtoList, pageable, orderSlice.hasNext());
  }
}
