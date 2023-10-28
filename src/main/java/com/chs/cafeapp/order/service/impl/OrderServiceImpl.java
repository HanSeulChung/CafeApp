package com.chs.cafeapp.order.service.impl;

import com.chs.cafeapp.cart.entity.Cart;
import com.chs.cafeapp.cart.entity.CartMenu;
import com.chs.cafeapp.cart.repository.CartMenusRepository;
import com.chs.cafeapp.cart.repository.CartRepository;
import com.chs.cafeapp.cart.service.CartMenuService;
import com.chs.cafeapp.menu.entity.Menus;
import com.chs.cafeapp.menu.repository.MenuRepository;
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
    User user = userRepository.findByLoginId(userId)
        .orElseThrow(() -> new RuntimeException("해당 사용자가 존재하지 않습니다."));
    Menus menus = menuRepository.findById(orderInput.getMenuId())
        .orElseThrow(() -> new RuntimeException("해당 메뉴가 존재하지 않습니다."));

    if (!menus.getName().equals(orderInput.getMenuName())) {
      throw new RuntimeException("메뉴 id와 이름이 맞지않습니다.");
    }

    menus.minusStock(orderInput.getQuantity());
    if(menus.getStock() == 0) {
      menus.setSoldOut(true);
    }
    menuRepository.save(menus);

    OrderedMenu saveOrderedMenu = orderedMenuRepository.save(
                                          OrderedMenu.builder()
                                                      .userId(userId)
                                                      .quantity(orderInput.getQuantity())
                                                      .totalPrice(orderInput.getMenuPrice() * orderInput.getQuantity())
                                                      .menus(menus)
                                                      .build());

    Order saveOrder = orderRepository.save(Order.builder()
                                                .couponUse(orderInput.isCouponUse())
                                                .user(user)
                                                .build());


    saveOrder.setOrderStatus(OrderStatus.PaySuccess);
    saveOrderedMenu.setOrder(saveOrder);
    orderedMenuRepository.save(saveOrderedMenu);

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
    List<OrderedMenu> orderedMenuList = new ArrayList<>();

    for (Long x : orderFromCartInput.getIdList()) {
      CartMenu cartMenu = cartMenusRepository.findById(x)
          .orElseThrow(() -> new RuntimeException("해당 되는 장바구니 메뉴가 없습니다."));

      Menus menus = menuRepository.findById(cartMenu.getMenus().getId())
          .orElseThrow(() -> new RuntimeException("해당되는 메뉴가 존재하지 않습니다."));

      menus.minusStock(cartMenu.getQuantity());
      if(menus.getStock() == 0) {
        menus.setSoldOut(true);
      }
      menuRepository.save(menus);

      OrderedMenu orderedMenu = OrderedMenu.fromCartMenu(cartMenu);
      orderedMenu.setOrder(saveOrder);
      orderedMenuList.add(orderedMenuRepository.save(orderedMenu));
      cartMenuService.deleteSpecificCartMenu(cart.getId(), cartMenu.getId(), userId);
    }
    saveOrder.setTotalPrice(orderedMenuList);
    saveOrder.setTotalQuantity(orderedMenuList);
    saveOrder.setOrderStatus(OrderStatus.PaySuccess);

    return OrderDto.of(orderRepository.save(saveOrder));
  }
}
