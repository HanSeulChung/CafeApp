package com.chs.cafeapp.order.service.impl;

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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
  private final OrderRepository orderRepository;
  private final OrderedMenuRepository orderedMenuRepository;
  private final MenuRepository menuRepository;
  private final UserRepository userRepository;
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

    Order fromOrderInput = Order.fromOrderInput(orderInput);
    fromOrderInput.setUser(user);
    fromOrderInput.setOrderStatus(OrderStatus.PaySuccess);
    OrderedMenu orderedMenu = OrderedMenu.builder()
                                        .totalPrice(orderInput.getMenuPrice() * orderInput.getQuantity())
                                        .quantity(orderInput.getQuantity())
                                        .userId(user.getLoginId())
                                        .build();
    orderedMenu.setMenus(menus);
    orderedMenu.setOrder(fromOrderInput);
    Order savedOrder = orderRepository.save(fromOrderInput);
    orderedMenuRepository.save(orderedMenu);

    return OrderDto.of(savedOrder);
  }

  @Override
  public OrderDto orderFromCart(OrderFromCartInput orderFromCartInput) {
    return null;
  }
}
