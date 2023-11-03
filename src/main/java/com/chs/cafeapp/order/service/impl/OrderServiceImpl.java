package com.chs.cafeapp.order.service.impl;

import static com.chs.cafeapp.exception.type.ErrorCode.ALREADY_PICKUP_SUCCESS;
import static com.chs.cafeapp.exception.type.ErrorCode.CAN_NOT_ORDER_THAN_STOCK;
import static com.chs.cafeapp.exception.type.ErrorCode.CART_MENU_NOT_FOUND;
import static com.chs.cafeapp.exception.type.ErrorCode.CART_NOT_FOUND;
import static com.chs.cafeapp.exception.type.ErrorCode.MENU_NOT_FOUND;
import static com.chs.cafeapp.exception.type.ErrorCode.ORDER_NOT_FOUND;
import static com.chs.cafeapp.exception.type.ErrorCode.USER_NOT_FOUND;

import com.chs.cafeapp.cart.entity.Cart;
import com.chs.cafeapp.cart.entity.CartMenu;
import com.chs.cafeapp.cart.repository.CartMenusRepository;
import com.chs.cafeapp.cart.repository.CartRepository;
import com.chs.cafeapp.cart.service.CartMenuService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
  private final MenuRepository menuRepository;
  private final UserRepository userRepository;
  private final CartRepository cartRepository;
  private final OrderRepository orderRepository;
  private final CartMenusRepository cartMenusRepository;
  private final OrderedMenuRepository orderedMenuRepository;

  private final CartMenuService cartMenuService;
  private final StampService stampService;

  @Override
  public OrderDto orderIndividualMenu(OrderInput orderInput, String userId) {

    User user = userRepository.findByLoginId(userId)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    Menus menus = menuRepository.findById(orderInput.getMenuId())
        .orElseThrow(() ->  new CustomException(MENU_NOT_FOUND));

    OrderedMenu saveOrderedMenu = orderedMenuRepository.save(
                                          OrderedMenu.builder()
                                                      .userId(userId)
                                                      .quantity(orderInput.getQuantity())
                                                      .totalPrice(orderInput.getMenuPrice() * orderInput.getQuantity())
                                                      .menus(menus)
                                                      .build());

    Order order = Order.builder()
                      .couponUse(orderInput.isCouponUse())
                      .user(user)
                      .totalQuantity(saveOrderedMenu.getQuantity())
                      .totalPrice(saveOrderedMenu.getTotalPrice())
                      .build();

    Order saveOrder = orderRepository.save(order);
    saveOrderedMenu.setOrder(saveOrder);
    saveOrder.setOrderStatus(OrderStatus.PaySuccess);
    orderedMenuRepository.save(saveOrderedMenu);


    menus.minusStock(orderInput.getQuantity());
    if(menus.getStock() == 0) {
      menus.setSoldOut(true);
    }
    menuRepository.save(menus);

    return OrderDto.of(orderRepository.save(saveOrder));
  }

  @Override
  public OrderDto orderFromCart(OrderFromCartInput orderFromCartInput, String userId) {
    User user = userRepository.findByLoginId(userId)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    Cart cart = cartRepository.findById(orderFromCartInput.getCartId())
        .orElseThrow(() -> new CustomException(CART_NOT_FOUND));

    if (orderFromCartInput.getIdList().size() == 1) {
      CartMenu cartMenu = cartMenusRepository.findById(orderFromCartInput.getIdList().get(0))
          .orElseThrow(() -> new CustomException(CART_MENU_NOT_FOUND));
      Menus menus = menuRepository.findById(cartMenu.getMenus().getId())
          .orElseThrow(() -> new CustomException(MENU_NOT_FOUND));

      OrderInput orderInput = new OrderInput(
                                      menus.getId(), menus.getName(), menus.getPrice(),
                                      cartMenu.getQuantity(), orderFromCartInput.isCouponUse());
      cartMenuService.deleteSpecificCartMenu(cart.getId(), cartMenu.getId(), userId);
      return orderIndividualMenu(orderInput, userId);
    }

    Order order = Order.builder()
                        .user(user)
                        .couponUse(orderFromCartInput.isCouponUse())
                        .build();

    Order saveOrder = orderRepository.save(order);
    Map<Menus, Integer> menusMap = new HashMap<>();

    for (Long cartMenuId : orderFromCartInput.getIdList()) {
      CartMenu cartMenu = cartMenusRepository.findById(cartMenuId)
          .orElseThrow(() -> new CustomException(CART_MENU_NOT_FOUND));

      Menus menus = menuRepository.findById(cartMenu.getMenus().getId())
          .orElseThrow(() -> new CustomException(MENU_NOT_FOUND));

      if (menus.getStock() - cartMenu.getQuantity() < 0) {
        throw new CustomException(CAN_NOT_ORDER_THAN_STOCK);
      }

      menusMap.put(menus, cartMenu.getQuantity());

      OrderedMenu orderedMenu = OrderedMenu.fromCartMenu(cartMenu, saveOrder);
      OrderedMenu saveOrderedMenu = orderedMenuRepository.save(orderedMenu);
      saveOrder.getOrderedMenus().add(saveOrderedMenu);
      cartMenuService.deleteSpecificCartMenu(cart.getId(), cartMenu.getId(), userId);
    }
    saveOrder.setTotalPrice(saveOrder.getOrderedMenus());
    saveOrder.setTotalQuantity(saveOrder.getOrderedMenus());
    saveOrder.setOrderStatus(OrderStatus.PaySuccess);

    if (menusMap != null) {
      for (Menus menus : menusMap.keySet()) {
        menus.minusStock(menusMap.get(menus));
        if(menus.getStock() == 0) {
          menus.setSoldOut(true);
        }
        menuRepository.save(menus);
      }
    }

    return OrderDto.of(orderRepository.save(saveOrder));

  }

  @Override
  public OrderDto orderAllFromCart(OrderAllFromCartInput orderAllFromCartInput, String userId) {
    User user = userRepository.findByLoginId(userId)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    Cart cart = cartRepository.findById(orderAllFromCartInput.getCartId())
        .orElseThrow(() -> new CustomException(CART_NOT_FOUND));

    Order order = Order.builder()
        .user(user)
        .couponUse(orderAllFromCartInput.isCouponUse())
        .build();
    Order saveOrder = orderRepository.save(order);

    Map<Menus, Integer> menusMap = new HashMap<>();
    for (CartMenu cartMenu : cart.getCartMenu()) {

      Menus menus = menuRepository.findById(cartMenu.getMenus().getId())
          .orElseThrow(() -> new CustomException(MENU_NOT_FOUND));

      if (menus.getStock() - cartMenu.getQuantity() < 0) {
        throw new CustomException(CAN_NOT_ORDER_THAN_STOCK);
      }

      menusMap.put(menus, cartMenu.getQuantity());

      OrderedMenu orderedMenu = OrderedMenu.fromCartMenu(cartMenu, saveOrder);
      OrderedMenu saveOrderedMenu = orderedMenuRepository.save(orderedMenu);
      saveOrder.getOrderedMenus().add(saveOrderedMenu);
    }

    cartMenuService.deleteAllCartMenu(orderAllFromCartInput.getCartId(), userId);

    saveOrder.setTotalPrice(saveOrder.getOrderedMenus());
    saveOrder.setTotalQuantity(saveOrder.getOrderedMenus());
    saveOrder.setOrderStatus(OrderStatus.PaySuccess);

    if (menusMap != null) {
      for (Menus menus : menusMap.keySet()) {
        menus.minusStock(menusMap.get(menus));
        if(menus.getStock() == 0) {
          menus.setSoldOut(true);
        }
        menuRepository.save(menus);
      }
    }
    return OrderDto.of(orderRepository.save(saveOrder));
  }

  @Override
  public OrderDto rejectOrder(long orderId) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new CustomException(ORDER_NOT_FOUND));

    if (!order.getOrderStatus().equals(OrderStatus.PaySuccess)) {
      throw new IllegalArgumentException();
    }

    order.setOrderStatus(OrderStatus.CancelByCafe);

    return OrderDto.of(orderRepository.save(order));
  }

  public Order validationOrder(long orderId) {
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

    Order order = validationOrder(orderId);

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
    Order order = validationOrder(orderId);
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
