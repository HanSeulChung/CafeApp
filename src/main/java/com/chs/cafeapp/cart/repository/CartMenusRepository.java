package com.chs.cafeapp.cart.repository;

import com.chs.cafeapp.cart.entity.CartMenu;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartMenusRepository extends JpaRepository<CartMenu, Long> {
  Optional<CartMenu> findByMenusId(long menuId);
}
