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
import java.util.ArrayList;
import java.util.List;
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

    // TO-DO: Order 엔티티 및 dto 관계 정리 후 기능 구현 다시
//    User user = userRepository.findByLoginId(userId)
//        .orElseThrow(() -> new RuntimeException("해당 사용자가 존재하지 않습니다."));
//    Menus menus = menuRepository.findById(orderInput.getMenuId())
//        .orElseThrow(() -> new RuntimeException("해당 메뉴가 존재하지 않습니다."));
//
//    if (!menus.getName().equals(orderInput.getMenuName())) {
//      throw new RuntimeException("메뉴 id와 이름이 맞지않습니다.");
//    }
//
//    menus.minusStock(orderInput.getQuantity());
//    if(menus.getStock() == 0) {
//      menus.setSoldOut(true);
//    }
//    menuRepository.save(menus);
//
//    Order fromOrderInput = Order.fromOrderInput(orderInput);
//    fromOrderInput.setUser(user);
//    fromOrderInput.setOrderStatus(OrderStatus.PaySuccess);
//    OrderedMenu orderedMenu = OrderedMenu.builder()
//                                        .totalPrice(orderInput.getMenuPrice() * orderInput.getQuantity())
//                                        .quantity(orderInput.getQuantity())
//                                        .userId(user.getLoginId())
//                                        .build();
//    orderedMenu.setMenus(menus);
//    orderedMenu.setOrder(fromOrderInput);
//    Order savedOrder = orderRepository.save(fromOrderInput);
//    orderedMenuRepository.save(orderedMenu);
//
//    return OrderDto.of(savedOrder);
    return OrderDto.builder().build();
  }

  @Override
  public OrderDto orderFromCart(OrderFromCartInput orderFromCartInput, String userId) {

    // TO-DO: Order 엔티티 및 dto 관계 정리 후 기능 구현 다시
//    User user = userRepository.findByLoginId(userId)
//        .orElseThrow(() -> new RuntimeException("해당 사용자가 존재하지 않습니다."));
//
//    Cart cart = cartRepository.findById(orderFromCartInput.getCartId())
//        .orElseThrow(() -> new RuntimeException("해당 카트가 존재하지 않습니다."));
//
//    if (orderFromCartInput.getIdList().size() == 1) {
//      CartMenu cartMenu = cartMenusRepository.findById(orderFromCartInput.getIdList().get(0))
//          .orElseThrow(() -> new RuntimeException("해당 되는 장바구니 메뉴가 없습니다."));
//      Menus menus = menuRepository.findById(cartMenu.getMenus().getId())
//          .orElseThrow(() -> new RuntimeException("해당되는 메뉴가 메뉴 목록에 존재하지 않습니다."));
//
//      OrderInput orderInput = new OrderInput(
//          menus.getId(), menus.getName(), menus.getPrice(),
//          cartMenu.getQuantity(), orderFromCartInput.isCouponUse());
//      cartMenuService.deleteSpecificCartMenu(cartMenu.getId(), userId);
//      return orderIndividualMenu(orderInput, userId);
//    }
//
//    Order order = Order.builder()
//                        .user(user)
//                        .couponUse(orderFromCartInput.isCouponUse())
//                        .build();
//
//    Order saveOrder = orderRepository.save(order);
//    List<OrderedMenu> orderedMenuList = new ArrayList<>();
//
//    for (Long x : orderFromCartInput.getIdList()) {
//      CartMenu cartMenu = cartMenusRepository.findById(x)
//          .orElseThrow(() -> new RuntimeException("해당 되는 장바구니 메뉴가 없습니다."));
//
//      Menus menus = menuRepository.findById(cartMenu.getMenus().getId())
//          .orElseThrow(() -> new RuntimeException("해당되는 메뉴가 존재하지 않습니다."));
//
//      menus.minusStock(cartMenu.getQuantity());
//      if(menus.getStock() == 0) {
//        menus.setSoldOut(true);
//      }
//      menuRepository.save(menus);
//
//      OrderedMenu orderedMenu = OrderedMenu.fromCartMenu(cartMenu);
//      orderedMenu.setOrder(saveOrder);
//      orderedMenuList.add(orderedMenuRepository.save(orderedMenu));
//      cartMenuService.deleteSpecificCartMenu(cartMenu.getId(), userId);
//    }
//    saveOrder.setTotalPrice(orderedMenuList);
//    saveOrder.setTotalQuantity(orderedMenuList);
//    saveOrder.setOrderStatus(OrderStatus.PaySuccess);
//
//    return OrderDto.of(orderRepository.save(saveOrder));

    return OrderDto.builder().build();

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

    List<OrderedMenu> orderedMenuList = new ArrayList<>();
    for (CartMenu cartMenu : cart.getCartMenu()) {

      Menus menus = menuRepository.findById(cartMenu.getMenus().getId())
          .orElseThrow(() -> new RuntimeException("해당되는 메뉴가 존재하지 않습니다."));

      menus.minusStock(cartMenu.getQuantity());
      if(menus.getStock() == 0) {
        menus.setSoldOut(true);
      }

      menuRepository.save(menus);

      OrderedMenu orderedMenu = OrderedMenu.fromCartMenu(cartMenu, saveOrder);
      orderedMenuRepository.save(orderedMenu);
      orderedMenuList.add(orderedMenu);
    }

    saveOrder.setTotalPrice(orderedMenuList);
    saveOrder.setTotalQuantity(orderedMenuList);
    saveOrder.setOrderStatus(OrderStatus.PaySuccess);
    cartMenuService.deleteAllCartMenu(orderAllFromCartInput.getCartId(), userId);
    return OrderDto.of(orderRepository.save(saveOrder));
  }
}
