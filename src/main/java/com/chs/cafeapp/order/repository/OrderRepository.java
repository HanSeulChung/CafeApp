package com.chs.cafeapp.order.repository;

import com.chs.cafeapp.order.entity.Order;
import com.chs.cafeapp.order.type.OrderStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

  // TODO: orderStatus의 num값으로 바로 찾을 수 있나?
//  List<Order> findAllByOrderStatusNum(int orderStatusNum);
  List<Order> findAllByOrderStatus(OrderStatus orderStatus);

}
