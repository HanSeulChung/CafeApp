package com.chs.cafeapp.config;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

  @Bean
  public Clock clock() {
    // 시스템 기본 시간대로 설정된 Clock 생성
    return Clock.systemDefaultZone();
  }
}
