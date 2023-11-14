package com.chs.cafeapp.auth.token.repository;

import com.chs.cafeapp.auth.token.entity.RefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
  Optional<RefreshToken> findByKey(String key);
  boolean existsByKey(String key);
  @Transactional
  void deleteAllByKey(String key);
}
