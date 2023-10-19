package com.chs.cafeapp.menu.repository;

import com.chs.cafeapp.menu.entity.Menus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface MenuRepository extends JpaRepository<Menus, Long> {
  boolean existsByName(String name);
  Optional<Menus> findMenusByName(String name);
  @Transactional
  void deleteById(Long menuId);
}
