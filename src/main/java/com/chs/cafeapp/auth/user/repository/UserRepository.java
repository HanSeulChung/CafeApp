package com.chs.cafeapp.auth.user.repository;

import com.chs.cafeapp.auth.user.entity.User;
import com.chs.cafeapp.auth.user.type.UserStatus;
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
  Optional<User> findByEmailAuthKey(String uuid);
  boolean existsByLoginId(String loginId);
  boolean existsByNickName(String nickName);
  List<User> findAllByUserStatusAndUpdateDateTimeLessThan(UserStatus userStatus, LocalDateTime nowLocalDateTime);

  @Transactional
  void deleteAllByUserStatusAndUpdateDateTimeLessThan(UserStatus userStatus, LocalDateTime nowLocalDateTime);

  @Modifying
  @Query("UPDATE User u SET u.userStatus = 'USER_STATUS_STOP' WHERE u.lastLoginDateTime < :cutoffDateTime")
  void updateUserStatusForOldLogins(@Param("cutoffDateTime") LocalDateTime cutoffDateTime);
}
