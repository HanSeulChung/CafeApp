package com.chs.cafeapp.domain.order.service.impl;

import static com.chs.cafeapp.global.exception.type.ErrorCode.CAN_NOT_ORDER_THAN_STOCK;
import static com.chs.cafeapp.global.exception.type.ErrorCode.COUPON_NOT_FOUND;
import static com.chs.cafeapp.global.exception.type.ErrorCode.EMPTY_SELECTED_CART_MENU;
import static com.chs.cafeapp.global.exception.type.ErrorCode.INVALID_REQUEST;
import static com.chs.cafeapp.domain.order.type.OrderStatus.CancelByUser;
import static com.chs.cafeapp.domain.order.type.OrderStatus.PaySuccess;

import com.chs.cafeapp.domain.cart.entity.Cart;
import com.chs.cafeapp.domain.cart.entity.CartMenu;
import com.chs.cafeapp.domain.cart.service.CartMenuService;
import com.chs.cafeapp.domain.coupon.entity.Coupon;
import com.chs.cafeapp.domain.coupon.repository.CouponRepository;
import com.chs.cafeapp.global.exception.CustomException;
import com.chs.cafeapp.domain.menu.entity.Menus;
import com.chs.cafeapp.domain.menu.repository.MenuRepository;
import com.chs.cafeapp.domain.order.dto.OrderAllFromCartInput;
import com.chs.cafeapp.domain.order.dto.OrderDto;
import com.chs.cafeapp.domain.order.dto.OrderFromCartInput;
import com.chs.cafeapp.domain.order.dto.OrderInput;
import com.chs.cafeapp.domain.order.entity.Order;
import com.chs.cafeapp.domain.order.entity.OrderedMenu;
import com.chs.cafeapp.domain.order.repository.OrderRepository;
import com.chs.cafeapp.domain.order.repository.OrderedMenuRepository;
import com.chs.cafeapp.domain.order.service.OrderMemberService;
import com.chs.cafeapp.domain.order.service.validation.ValidationCheck;
import com.chs.cafeapp.auth.member.entity.Member;
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
public class OrderMemberServiceImpl implements OrderMemberService {

  private final MenuRepository menuRepository;
  private final OrderRepository orderRepository;
  private final CouponRepository couponRepository;
  private final OrderedMenuRepository orderedMenuRepository;

  private final CartMenuService cartMenuService;
  private final ValidationCheck validationCheck;

  @Override
  @Transactional
  public OrderDto orderIndividualMenu(OrderInput orderInput, String memberId) {


    Member user = validationCheck.validationUser(memberId);
    Menus menus = validationCheck.validationMenus(orderInput.getMenuId());

    OrderedMenu saveOrderedMenu = orderedMenuRepository.save(
        OrderedMenu.builder()
            .userId(memberId)
            .quantity(orderInput.getQuantity())
            .totalPrice(orderInput.getMenuPrice() * orderInput.getQuantity())
            .menus(menus)
            .build());

    Order buildOrder = Order.builder()
        .couponUse(orderInput.isCouponUse())
        .member(user)
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
  public OrderDto orderFromCart(OrderFromCartInput orderFromCartInput, String memberId) {

    Member user = validationCheck.validationUser(memberId);
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

      cartMenuService.deleteSpecificCartMenu(cart.getId(), cartMenu.getId(), memberId);

      return orderIndividualMenu(orderInput, memberId);
    }

    Order buildOrder = Order.builder()
        .member(user)
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
      cartMenuService.deleteSpecificCartMenu(cart.getId(), cartMenu.getId(), memberId);
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
  public OrderDto orderAllFromCart(OrderAllFromCartInput orderAllFromCartInput, String memberId) {
    Member user = validationCheck.validationUser(memberId);
    Cart cart = validationCheck.validationCart(orderAllFromCartInput.getCartId());

    Order buildOrder = Order.builder()
        .member(user)
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

    cartMenuService.deleteAllCartMenu(orderAllFromCartInput.getCartId(), memberId);

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
    Member user = validationCheck.validationUser(userId);
    Slice<Order> orderSlice = orderRepository.findAllByMemberId(pageable, user.getId());
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
  public Slice<OrderDto> viewAllOrdersDuringDays(String memberId, Pageable pageable) {
    LocalDate nowDate = LocalDate.now();
    LocalDateTime nowDateTime = nowDate.atTime(23, 59, 59);
    Member user = validationCheck.validationUser(memberId);
    Slice<Order> orderSlice = orderRepository.findAllByMemberIdAndCreateDateTimeBetween(
        user.getId(),
        nowDateTime.minusDays(1), nowDateTime,
        pageable);
    List<OrderDto> orderDtoList = OrderDto.convertListDtoFromPageEntity(orderSlice);
    return new SliceImpl<>(orderDtoList, pageable, orderSlice.hasNext());
  }

  @Override
  public Slice<OrderDto> viewAllOrdersDuringWeeks(String memberId, Pageable pageable) {
    LocalDate nowDate = LocalDate.now();
    LocalDateTime nowDateTime = nowDate.atTime(23, 59, 59);
    Member user = validationCheck.validationUser(memberId);
    Slice<Order> orderSlice = orderRepository.findAllByMemberIdAndCreateDateTimeBetween(
        user.getId(),
        nowDateTime.minusDays(7), nowDateTime,
        pageable);
    List<OrderDto> orderDtoList = OrderDto.convertListDtoFromPageEntity(orderSlice);
    return new SliceImpl<>(orderDtoList, pageable, orderSlice.hasNext());
  }

  @Override
  public Slice<OrderDto> viewAllOrdersDuringMonths(String memberId, Pageable pageable) {
    LocalDate nowDate = LocalDate.now();
    LocalDateTime nowDateTime = nowDate.atTime(23, 59, 59);
    Member user = validationCheck.validationUser(memberId);
    Slice<Order> orderSlice = orderRepository.findAllByMemberIdAndCreateDateTimeBetween(
        user.getId(),
        nowDateTime.minusMonths(1), nowDateTime,
        pageable);
    List<OrderDto> orderDtoList = OrderDto.convertListDtoFromPageEntity(orderSlice);
    return new SliceImpl<>(orderDtoList, pageable, orderSlice.hasNext());
  }

  @Override
  public Slice<OrderDto> viewAllOrdersDuringThreeMonths(String memberId, Pageable pageable) {
    LocalDate nowDate = LocalDate.now();
    LocalDateTime nowDateTime = nowDate.atTime(23, 59, 59);
    Member user = validationCheck.validationUser(memberId);
    Slice<Order> orderSlice = orderRepository.findAllByMemberIdAndCreateDateTimeBetween(
        user.getId(),
        nowDateTime.minusMonths(3), nowDateTime,
        pageable);
    List<OrderDto> orderDtoList = OrderDto.convertListDtoFromPageEntity(orderSlice);
    return new SliceImpl<>(orderDtoList, pageable, orderSlice.hasNext());
  }

  @Override
  public Slice<OrderDto> viewAllOrdersDuringSixMonths(String memberId, Pageable pageable) {
    LocalDate nowDate = LocalDate.now();
    LocalDateTime nowDateTime = nowDate.atTime(23, 59, 59);
    Member user = validationCheck.validationUser(memberId);
    Slice<Order> orderSlice = orderRepository.findAllByMemberIdAndCreateDateTimeBetween(
        user.getId(),
        nowDateTime.minusMonths(6), nowDateTime,
        pageable);
    List<OrderDto> orderDtoList = OrderDto.convertListDtoFromPageEntity(orderSlice);
    return new SliceImpl<>(orderDtoList, pageable, orderSlice.hasNext());
  }

  @Override
  public Slice<OrderDto> viewAllOrdersDuringYears(String memberId, Pageable pageable) {
    LocalDate nowDate = LocalDate.now();
    LocalDateTime nowDateTime = nowDate.atTime(23, 59, 59);
    Member user = validationCheck.validationUser(memberId);
    Slice<Order> orderSlice = orderRepository.findAllByMemberIdAndCreateDateTimeBetween(
        user.getId(),
        nowDateTime.minusYears(1), nowDateTime,
        pageable);
    List<OrderDto> orderDtoList = OrderDto.convertListDtoFromPageEntity(orderSlice);
    return new SliceImpl<>(orderDtoList, pageable, orderSlice.hasNext());
  }
}
