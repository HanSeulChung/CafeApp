package com.chs.cafeapp.order.repository;

import com.chs.cafeapp.order.entity.OrderedMenu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderedMenuRepository extends JpaRepository<OrderedMenu, Long> {

}
