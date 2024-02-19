package com.chs.cafeapp.order.service.impl;

import static com.chs.cafeapp.exception.type.ErrorCode.ALREADY_PICKUP_SUCCESS;
import static com.chs.cafeapp.exception.type.ErrorCode.COUPON_NOT_FOUND;
import static com.chs.cafeapp.exception.type.ErrorCode.ORDER_NOT_FOUND;
import static com.chs.cafeapp.order.type.OrderStatus.CancelByCafe;
import static com.chs.cafeapp.order.type.OrderStatus.PickUpSuccess;
import static com.chs.cafeapp.order.type.OrderStatus.PreParingMenus;
import static com.chs.cafeapp.order.type.OrderStatus.WaitingPickUp;

import com.chs.cafeapp.coupon.entity.Coupon;
import com.chs.cafeapp.coupon.repository.CouponRepository;
import com.chs.cafeapp.exception.CustomException;
import com.chs.cafeapp.menu.entity.Menus;
import com.chs.cafeapp.menu.repository.MenuRepository;
import com.chs.cafeapp.order.dto.OrderDto;
import com.chs.cafeapp.order.entity.Order;
import com.chs.cafeapp.order.entity.OrderedMenu;
import com.chs.cafeapp.order.repository.OrderRepository;
import com.chs.cafeapp.order.service.OrderAdminService;
import com.chs.cafeapp.order.service.validation.ValidationCheck;
import com.chs.cafeapp.order.type.OrderStatus;
import com.chs.cafeapp.stamp.service.StampService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderAdminServiceImpl implements OrderAdminService {
  private final MenuRepository menuRepository;
  private final OrderRepository orderRepository;
  private final CouponRepository couponRepository;

  private final StampService stampService;

  private final ValidationCheck validationCheck;
  @Override
  public OrderDto rejectOrder(long orderId) {
    Order order = validationCheck.validationOrder(orderId);

    order.setOrderStatus(CancelByCafe);
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

    return order;
  }
  @Override
  public OrderDto changeOrderStatus(long orderId) {

    Order order = validationChangeOrderStatus(orderId);

    OrderStatus orderStatus = order.getOrderStatus();
    switch (orderStatus) {
      case PaySuccess:
        order.setOrderStatus(PreParingMenus);
        break;
      case PreParingMenus:
        order.setOrderStatus(WaitingPickUp);
        break;
      case WaitingPickUp:
        order.setOrderStatus(PickUpSuccess);
        long drinksCnt = order.getOrderedMenus().stream()
            .filter(orderedMenu -> "음료".equals(orderedMenu.getMenus().getCategory().getSuperCategory()))
            .mapToLong(orderedMenu -> orderedMenu.getQuantity())
            .sum();
        stampService.addStampNumbers(drinksCnt, order.getMember().getLoginId());
        break;
      case PickUpSuccess:
        throw new CustomException(ALREADY_PICKUP_SUCCESS);
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
  public Slice<OrderDto> viewAllOrders(Pageable pageable) {
    Slice<Order> orderSlice = orderRepository.findAll(pageable);
    List<OrderDto> orderDtoList = OrderDto.convertListDtoFromPageEntity(orderSlice);
    return new SliceImpl<>(orderDtoList, pageable, orderSlice.hasNext());
  }

  @Override
  public Slice<OrderDto> viewAllOrdersDuringDays(Pageable pageable) {
    LocalDate nowDate = LocalDate.now();
    LocalDateTime nowDateTime = nowDate.atTime(23, 59, 59);
    Slice<Order> orderSlice = orderRepository.findAllByCreateDateTimeBetween(
        nowDateTime.minusDays(1), nowDateTime, pageable);
    List<OrderDto> orderDtoList = OrderDto.convertListDtoFromPageEntity(orderSlice);
    return new SliceImpl<>(orderDtoList, pageable, orderSlice.hasNext());
  }

  @Override
  public Slice<OrderDto> viewAllOrdersDuringWeeks(Pageable pageable) {
    LocalDate nowDate = LocalDate.now();
    LocalDateTime nowDateTime = nowDate.atTime(23, 59, 59);
    Slice<Order> orderSlice = orderRepository.findAllByCreateDateTimeBetween(
        nowDateTime.minusDays(7), nowDateTime, pageable);
    List<OrderDto> orderDtoList = OrderDto.convertListDtoFromPageEntity(orderSlice);
    return new SliceImpl<>(orderDtoList, pageable, orderSlice.hasNext());
  }

  @Override
  public Slice<OrderDto> viewAllOrdersDuringMonths(Pageable pageable) {
    LocalDate nowDate = LocalDate.now();
    LocalDateTime nowDateTime = nowDate.atTime(23, 59, 59);
    Slice<Order> orderSlice = orderRepository.findAllByCreateDateTimeBetween(
        nowDateTime.minusMonths(1), nowDateTime, pageable);
    List<OrderDto> orderDtoList = OrderDto.convertListDtoFromPageEntity(orderSlice);
    return new SliceImpl<>(orderDtoList, pageable, orderSlice.hasNext());
  }

  @Override
  public Slice<OrderDto> viewAllOrdersDuringThreeMonths(Pageable pageable) {
    LocalDate nowDate = LocalDate.now();
    LocalDateTime nowDateTime = nowDate.atTime(23, 59, 59);
    Slice<Order> orderSlice = orderRepository.findAllByCreateDateTimeBetween(
        nowDateTime.minusMonths(3), nowDateTime, pageable);
    List<OrderDto> orderDtoList = OrderDto.convertListDtoFromPageEntity(orderSlice);
    return new SliceImpl<>(orderDtoList, pageable, orderSlice.hasNext());
  }

  @Override
  public Slice<OrderDto> viewAllOrdersDuringSixMonths(Pageable pageable) {
    LocalDate nowDate = LocalDate.now();
    LocalDateTime nowDateTime = nowDate.atTime(23, 59, 59);
    Slice<Order> orderSlice = orderRepository.findAllByCreateDateTimeBetween(
        nowDateTime.minusMonths(6), nowDateTime, pageable);
    List<OrderDto> orderDtoList = OrderDto.convertListDtoFromPageEntity(orderSlice);
    return new SliceImpl<>(orderDtoList, pageable, orderSlice.hasNext());
  }

  @Override
  public Slice<OrderDto> viewAllOrdersDuringYears(Pageable pageable) {
    LocalDate nowDate = LocalDate.now();
    LocalDateTime nowDateTime = nowDate.atTime(23, 59, 59);
    Slice<Order> orderSlice = orderRepository.findAllByCreateDateTimeBetween(
        nowDateTime.minusYears(1), nowDateTime, pageable);
    List<OrderDto> orderDtoList = OrderDto.convertListDtoFromPageEntity(orderSlice);
    return new SliceImpl<>(orderDtoList, pageable, orderSlice.hasNext());
  }

  @Override
  public Slice<OrderDto> viewOrdersByOrderStatus(int orderStatusNum, Pageable pageable) {
    OrderStatus byNumOrderStatus = OrderStatus.findByNum(orderStatusNum);
    Slice<Order> orderSlice = orderRepository.findAllByOrderStatus(byNumOrderStatus, pageable);
    List<OrderDto> orderDtoList = OrderDto.convertListDtoFromPageEntity(orderSlice);
    return new SliceImpl<>(orderDtoList, pageable, orderSlice.hasNext());
  }

  @Override
  public Slice<OrderDto> viewOrdersByOrderStatusDuringDays(int orderStatusNum, Pageable pageable) {
    OrderStatus byNumOrderStatus = OrderStatus.findByNum(orderStatusNum);

    LocalDate nowDate = LocalDate.now();
    LocalDateTime nowDateTime = nowDate.atTime(23, 59, 59);

    Slice<Order> orderSlice = orderRepository.findAllByOrderStatusAndCreateDateTimeBetween(
        byNumOrderStatus, nowDateTime.minusDays(1), nowDateTime, pageable);

    List<OrderDto> orderDtoList = OrderDto.convertListDtoFromPageEntity(orderSlice);
    return new SliceImpl<>(orderDtoList, pageable, orderSlice.hasNext());
  }

  @Override
  public Slice<OrderDto> viewOrdersByOrderStatusDuringWeeks(int orderStatusNum, Pageable pageable) {
    OrderStatus byNumOrderStatus = OrderStatus.findByNum(orderStatusNum);

    LocalDate nowDate = LocalDate.now();
    LocalDateTime nowDateTime = nowDate.atTime(23, 59, 59);

    Slice<Order> orderSlice = orderRepository.findAllByOrderStatusAndCreateDateTimeBetween(
        byNumOrderStatus, nowDateTime.minusDays(7), nowDateTime, pageable);

    List<OrderDto> orderDtoList = OrderDto.convertListDtoFromPageEntity(orderSlice);
    return new SliceImpl<>(orderDtoList, pageable, orderSlice.hasNext());
  }

  @Override
  public Slice<OrderDto> viewOrdersByOrderStatusDuringMonths(int orderStatusNum, Pageable pageable) {
    OrderStatus byNumOrderStatus = OrderStatus.findByNum(orderStatusNum);

    LocalDate nowDate = LocalDate.now();
    LocalDateTime nowDateTime = nowDate.atTime(23, 59, 59);

    Slice<Order> orderSlice = orderRepository.findAllByOrderStatusAndCreateDateTimeBetween(
        byNumOrderStatus, nowDateTime.minusMonths(1), nowDateTime, pageable);

    List<OrderDto> orderDtoList = OrderDto.convertListDtoFromPageEntity(orderSlice);
    return new SliceImpl<>(orderDtoList, pageable, orderSlice.hasNext());
  }

  @Override
  public Slice<OrderDto> viewOrdersByOrderStatusDuringThreeMonths(int orderStatusNum,
      Pageable pageable) {
    OrderStatus byNumOrderStatus = OrderStatus.findByNum(orderStatusNum);

    LocalDate nowDate = LocalDate.now();
    LocalDateTime nowDateTime = nowDate.atTime(23, 59, 59);

    Slice<Order> orderSlice = orderRepository.findAllByOrderStatusAndCreateDateTimeBetween(
        byNumOrderStatus, nowDateTime.minusMonths(3), nowDateTime, pageable);

    List<OrderDto> orderDtoList = OrderDto.convertListDtoFromPageEntity(orderSlice);
    return new SliceImpl<>(orderDtoList, pageable, orderSlice.hasNext());
  }

  @Override
  public Slice<OrderDto> viewOrdersByOrderStatusDuringSixMonths(int orderStatusNum,
      Pageable pageable) {
    OrderStatus byNumOrderStatus = OrderStatus.findByNum(orderStatusNum);

    LocalDate nowDate = LocalDate.now();
    LocalDateTime nowDateTime = nowDate.atTime(23, 59, 59);

    Slice<Order> orderSlice = orderRepository.findAllByOrderStatusAndCreateDateTimeBetween(
        byNumOrderStatus, nowDateTime.minusMonths(6), nowDateTime, pageable);

    List<OrderDto> orderDtoList = OrderDto.convertListDtoFromPageEntity(orderSlice);
    return new SliceImpl<>(orderDtoList, pageable, orderSlice.hasNext());
  }

  @Override
  public Slice<OrderDto> viewOrdersByOrderStatusDuringYears(int orderStatusNum, Pageable pageable) {
    OrderStatus byNumOrderStatus = OrderStatus.findByNum(orderStatusNum);

    LocalDate nowDate = LocalDate.now();
    LocalDateTime nowDateTime = nowDate.atTime(23, 59, 59);

    Slice<Order> orderSlice = orderRepository.findAllByOrderStatusAndCreateDateTimeBetween(
        byNumOrderStatus, nowDateTime.minusYears(1), nowDateTime, pageable);

    List<OrderDto> orderDtoList = OrderDto.convertListDtoFromPageEntity(orderSlice);
    return new SliceImpl<>(orderDtoList, pageable, orderSlice.hasNext());
  }
}
