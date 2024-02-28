package com.chs.cafeapp.domain.order.repository;

import com.chs.cafeapp.domain.order.entity.OrderedMenu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderedMenuRepository extends JpaRepository<OrderedMenu, Long> {

}
