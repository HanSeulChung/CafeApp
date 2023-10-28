package com.chs.cafeapp.cart.repository;

import com.chs.cafeapp.cart.entity.CartMenu;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CartMenusRepository extends JpaRepository<CartMenu, Long> {
  Optional<CartMenu> findByMenusId(long menuId);
  Optional<CartMenu> findByCartId(long cartId);
  @Transactional
  void deleteById(Long cartMenuId);

  @Transactional
  void deleteAllByCartId(Long cartId);

}
