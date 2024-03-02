package com.chs.cafeapp.global.redis.token;

import static com.chs.cafeapp.auth.token.constant.TokenConstant.ACCESS_TOKEN_EXPIRE_TIME;
import static com.chs.cafeapp.auth.token.constant.TokenConstant.REFRESH_TOKEN_EXPIRE_TIME;
import static com.chs.cafeapp.global.redis.constant.RedisKeyPrefix.ACCESS_TOKEN;
import static com.chs.cafeapp.global.redis.constant.RedisKeyPrefix.INVALID_ACCESS_TOKEN;
import static com.chs.cafeapp.global.redis.constant.RedisKeyPrefix.REFRESH_TOKEN;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TokenRepository {
  private final StringRedisTemplate redisTemplate;

  public void saveAccessToken(String userId, String refreshToken) {

    if (redisTemplate != null) {
      String key = ACCESS_TOKEN + userId;
      redisTemplate.opsForValue().set(key, refreshToken, Duration.ofSeconds(REFRESH_TOKEN_EXPIRE_TIME));
    } else {
      log.error("StringRedisTemplate is null. Check configuration.");
      throw new IllegalStateException("StringRedisTemplate is null. Check configuration.");
    }
  }

  public void saveRefreshToken(String userId, String refreshToken) {

    if (redisTemplate != null) {
      String key = REFRESH_TOKEN + userId;
      redisTemplate.opsForValue().set(key, refreshToken, Duration.ofSeconds(REFRESH_TOKEN_EXPIRE_TIME));
    } else {
      log.error("StringRedisTemplate is null. Check configuration.");
      throw new IllegalStateException("StringRedisTemplate is null. Check configuration.");
    }
  }

  public void saveInValidAccessToken(String userId, String invalidAccessToken) {
    if (redisTemplate != null) {
      String key = INVALID_ACCESS_TOKEN + userId;
      redisTemplate.opsForValue().set(key, invalidAccessToken, Duration.ofSeconds(ACCESS_TOKEN_EXPIRE_TIME));
    } else {
      log.error("StringRedisTemplate is null. Check configuration.");
      throw new IllegalStateException("StringRedisTemplate is null. Check configuration.");
    }
  }

  public String getAccessToken(String userId) {
    String key = ACCESS_TOKEN + userId;
    return redisTemplate.opsForValue().get(key);
  }

  public String getRefreshToken(String userId) {
    String key = REFRESH_TOKEN + userId;
    return redisTemplate.opsForValue().get(key);
  }

  public String getInvalidAccessToken(String userId) {
    String key = INVALID_ACCESS_TOKEN + userId;
    return redisTemplate.opsForValue().get(key);
  }
  public void deleteAccessToken(String userId) {
    String key = ACCESS_TOKEN + userId;
    redisTemplate.delete(key);
  }

  public void deleteRefreshToken(String userId) {
    String key = REFRESH_TOKEN + userId;
    redisTemplate.delete(key);
  }

  public boolean checkInValidAccessToken(String userId, String token) {
    if (getInvalidAccessToken(userId) != null) {
      return getInvalidAccessToken(userId).equals(token);
    }
    return false;
  }
}
