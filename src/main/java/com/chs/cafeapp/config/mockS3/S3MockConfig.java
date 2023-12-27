package com.chs.cafeapp.config.mockS3;

import io.findify.s3mock.S3Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("dev")
@Configuration
public class S3MockConfig {

  @Value("${embedded.aws.s3.mock.port}")
  private int port;

  @Bean
  public S3Mock s3Mock() {
    return new S3Mock.Builder()
        .withPort(port)
        .withInMemoryBackend()
        .build();
  }
}
