package com.chs.cafeapp.order.repository;

import com.chs.cafeapp.order.entity.Order;
import com.chs.cafeapp.order.type.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface OrderRepository extends JpaRepository<Order, Long> {

  List<Order> findAllByOrderStatus(OrderStatus orderStatus);
  List<Order> findAllByUpdateDateTimeLessThan(LocalDateTime localDateTime);
  Slice<Order> findAllByCreateDateTimeBetween(LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable);
  Slice<Order> findAllByOrderStatus(OrderStatus orderStatus, Pageable pageable);
  Slice<Order> findAllByOrderStatusAndCreateDateTimeBetween(OrderStatus orderStatus, LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable);

  Slice<Order> findAllByMemberId(Pageable pageable, long memberId);
  Slice<Order> findAllByMemberIdAndCreateDateTimeBetween(long memberId, LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable);

  @Transactional
  void deleteAllByUpdateDateTimeLessThan(LocalDateTime nowLocalDateTimeMinusOneYears);
}
