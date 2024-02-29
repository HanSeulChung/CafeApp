package com.chs.cafeapp.global.redis.auth;

import static com.chs.cafeapp.global.mail.MailConstant.EMAIL_VERIFICATION_LIMIT_IN_SECONDS;
import static com.chs.cafeapp.global.redis.constant.RedisKeyPrefix.EMAIL_AUTH;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Slf4j
@Component
@RequiredArgsConstructor
public class CertifiedNumberAuthRepository {
  private final StringRedisTemplate redisTemplate;

  public void saveCertificationNumber(String email, String certificationNumber) {
    if (redisTemplate != null) {
      redisTemplate.opsForValue()
          .set(EMAIL_AUTH + email, certificationNumber, Duration.ofSeconds(EMAIL_VERIFICATION_LIMIT_IN_SECONDS));
    } else {
      log.error("StringRedisTemplate is null. Check configuration.");
      throw new IllegalStateException("StringRedisTemplate is null. Check configuration.");
    }
  }

  public String getCertificationNumber(String email) {
    return redisTemplate.opsForValue().get(EMAIL_AUTH + email);
  }

  public void removeCertificationNumber(String email) {
    redisTemplate.delete(EMAIL_AUTH + email);
  }

  public boolean hasKey(String email) {
    Boolean keyExists = redisTemplate.hasKey(EMAIL_AUTH + email);
    return keyExists != null && keyExists;
  }

}
