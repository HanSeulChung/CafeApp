package com.chs.cafeapp.user.repository;

import com.chs.cafeapp.user.entity.User;
import com.chs.cafeapp.user.type.UserStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByLoginId(String loginId);

  List<User> findAllByUserStatusAndUpdateDateTimeLessThan(UserStatus userStatus, LocalDateTime nowLocalDateTime);

  @Transactional
  void deleteAllByUserStatusAndUpdateDateTimeLessThan(UserStatus userStatus, LocalDateTime nowLocalDateTime);

  @Modifying
  @Query("UPDATE User u SET u.userStatus = 'USER_STATUS_STOP' WHERE u.lastLoginDateTime < :cutoffDateTime")
  void updateUserStatusForOldLogins(@Param("cutoffDateTime") LocalDateTime cutoffDateTime);
}
