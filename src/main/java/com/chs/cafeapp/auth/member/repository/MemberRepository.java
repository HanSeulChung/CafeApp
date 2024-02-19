package com.chs.cafeapp.auth.member.repository;

import com.chs.cafeapp.auth.member.entity.Member;
import com.chs.cafeapp.auth.member.type.MemberStatus;
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
public interface MemberRepository extends JpaRepository<Member, Long> {
  Optional<Member> findByLoginId(String loginId);
  Optional<Member> findByEmailAuthKey(String uuid);
  boolean existsByLoginId(String loginId);
  boolean existsByNickName(String nickName);
  List<Member> findAllByUserStatusAndUpdateDateTimeLessThan(MemberStatus userStatus, LocalDateTime nowLocalDateTime);

  @Transactional
  void deleteAllByUserStatusAndUpdateDateTimeLessThan(MemberStatus userStatus, LocalDateTime nowLocalDateTime);

  @Modifying
  @Query("UPDATE Member u SET u.memberStatus = 'USER_STATUS_STOP' WHERE u.lastLoginDateTime < :cutoffDateTime")
  void updateUserStatusForOldLogins(@Param("cutoffDateTime") LocalDateTime cutoffDateTime);
}
