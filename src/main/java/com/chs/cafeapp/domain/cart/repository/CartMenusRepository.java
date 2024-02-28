package com.chs.cafeapp.domain.cart.repository;

import com.chs.cafeapp.domain.cart.entity.CartMenu;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CartMenusRepository extends JpaRepository<CartMenu, Long> {
  Optional<CartMenu> findByMenusId(long menuId);
  Optional<CartMenu> findByCartId(long cartId);
  List<CartMenu> findAllByCartId(long cartId);
  Page<CartMenu> findAllByCartId(long cartId, Pageable pageable);
  boolean existsByMenusId(long menuId);
  @Transactional
  void deleteById(Long cartMenuId);

  @Transactional
  void deleteAllByCartId(Long cartId);
}
