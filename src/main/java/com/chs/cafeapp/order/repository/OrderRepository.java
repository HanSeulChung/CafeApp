package com.chs.cafeapp.order.repository;

import com.chs.cafeapp.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
