package com.chs.cafeapp.order.service.impl;

import com.chs.cafeapp.cart.entity.Cart;
import com.chs.cafeapp.cart.entity.CartMenu;
import com.chs.cafeapp.cart.repository.CartMenusRepository;
import com.chs.cafeapp.cart.repository.CartRepository;
import com.chs.cafeapp.cart.service.CartMenuService;
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

  @Override
  public OrderDto orderIndividualMenu(OrderInput orderInput, String userId) {

    User user = userRepository.findByLoginId(userId)
        .orElseThrow(() -> new RuntimeException("해당 사용자가 존재하지 않습니다."));
    Menus menus = menuRepository.findById(orderInput.getMenuId())
        .orElseThrow(() -> new RuntimeException("해당 메뉴가 존재하지 않습니다."));

    if (!menus.getName().equals(orderInput.getMenuName())) {
      throw new RuntimeException("메뉴 id와 이름이 맞지않습니다.");
    }

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
        .orElseThrow(() -> new RuntimeException("해당 사용자가 존재하지 않습니다."));

    Cart cart = cartRepository.findById(orderFromCartInput.getCartId())
        .orElseThrow(() -> new RuntimeException("해당 장바구니가 존재하지 않습니다."));

    if (orderFromCartInput.getIdList().size() == 1) {
      CartMenu cartMenu = cartMenusRepository.findById(orderFromCartInput.getIdList().get(0))
          .orElseThrow(() -> new RuntimeException("해당 되는 장바구니 메뉴가 없습니다."));
      Menus menus = menuRepository.findById(cartMenu.getMenus().getId())
          .orElseThrow(() -> new RuntimeException("해당되는 메뉴가 메뉴 목록에 존재하지 않습니다."));

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
          .orElseThrow(() -> new RuntimeException("해당 되는 장바구니 메뉴가 없습니다."));

      Menus menus = menuRepository.findById(cartMenu.getMenus().getId())
          .orElseThrow(() -> new RuntimeException("해당되는 메뉴가 존재하지 않습니다."));

      if (menus.getStock() - cartMenu.getQuantity() < 0) {
        throw new RuntimeException("메뉴의 재고이상 주문할 수 없습니다.");
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
        .orElseThrow(() -> new RuntimeException("해당 사용자가 존재하지 않습니다."));

    Cart cart = cartRepository.findById(orderAllFromCartInput.getCartId())
        .orElseThrow(() -> new RuntimeException("해당 장바구니가 존재하지 않습니다."));

    Order order = Order.builder()
        .user(user)
        .couponUse(orderAllFromCartInput.isCouponUse())
        .build();
    Order saveOrder = orderRepository.save(order);

    Map<Menus, Integer> menusMap = new HashMap<>();
    for (CartMenu cartMenu : cart.getCartMenu()) {

      Menus menus = menuRepository.findById(cartMenu.getMenus().getId())
          .orElseThrow(() -> new RuntimeException("해당되는 메뉴가 존재하지 않습니다."));

      if (menus.getStock() - cartMenu.getQuantity() < 0) {
        throw new RuntimeException("메뉴의 재고이상 주문할 수 없습니다.");
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
  public List<OrderDto> viewAllOrders() {
    return OrderDto.of(orderRepository.findAll());
  }

  @Override
  public List<OrderDto> viewOrdersByOrderStatus(int orderStatusNum) {
    OrderStatus byNumOrderStatus = OrderStatus.findByNum(orderStatusNum);
    return OrderDto.of(orderRepository.findAllByOrderStatus(byNumOrderStatus));
  }
}
