package com.chs.cafeapp.cart.repository;

import com.chs.cafeapp.cart.entity.CartMenu;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface CartMenusRepository extends JpaRepository<CartMenu, Long> {
  Optional<CartMenu> findByMenusId(long menuId);

  @Transactional
  void deleteById(Long cartMenuId);

  @Transactional
  void deleteAllByCartId(Long cartId);
}
