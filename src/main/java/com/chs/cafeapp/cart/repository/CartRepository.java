package com.chs.cafeapp.cart.repository;

import com.chs.cafeapp.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

}
