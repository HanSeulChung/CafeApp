package com.chs.cafeapp.global.redis;

import static com.chs.cafeapp.global.mail.MailConstant.EMAIL_VERIFICATION_LIMIT_IN_SECONDS;

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
          .set(email, certificationNumber, Duration.ofSeconds(EMAIL_VERIFICATION_LIMIT_IN_SECONDS));
    } else {
      log.error("StringRedisTemplate is null. Check configuration.");
      throw new IllegalStateException("StringRedisTemplate is null. Check configuration.");
    }
  }

  public String getCertificationNumber(String email) {
    return redisTemplate.opsForValue().get(email);
  }

  public void removeCertificationNumber(String email) {
    redisTemplate.delete(email);
  }

  public boolean hasKey(String email) {
    Boolean keyExists = redisTemplate.hasKey(email);
    return keyExists != null && keyExists;
  }

}
