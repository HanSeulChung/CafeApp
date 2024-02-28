package com.chs.cafeapp.config;

import com.chs.cafeapp.global.config.redis.RedisConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class RedisConfigTest {

  @Autowired
  private RedisConfig redisConfig;
  @Autowired
  private RedisConnectionFactory redisConnectionFactory;
  @Test
  public void testRedisConnectionFactoryHostAndPort() {
    // RedisConfig 클래스에서 가져온 호스트 및 포트 값 확인
    String host = redisConfig.getHost();
    int port = redisConfig.getPort();

    // 가져온 호스트 및 포트 값이 null이 아니고 기대한 값과 일치하는지 확인
    assertNotNull(host);
    assertEquals("localhost", host); // 기대한 호스트 값과 일치하는지 확인
    assertEquals(6379, port); // 기대한 포트 값과 일치하는지 확인
  }

  @Test
  public void testRedisConnectionFactoryCreation() {
    // RedisConfig 클래스에서 생성한 RedisConnectionFactory 확인
    RedisConnectionFactory connectionFactory = redisConfig.redisConnectionFactory();

    // 생성된 RedisConnectionFactory가 null이 아닌지 확인
    assertNotNull(connectionFactory);
    System.out.println(connectionFactory.getConnection());
    System.out.println(connectionFactory.getConnection().getNativeConnection());
    // 기대한 호스트 및 포트 값과 일치하는지 확인
//    assertNotNull(connectionFactory.getConnection());
//    assertNotNull(connectionFactory.getConnection().getNativeConnection());
  }
}

