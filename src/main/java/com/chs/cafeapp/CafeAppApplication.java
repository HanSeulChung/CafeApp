package com.chs.cafeapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@EnableScheduling
@EnableJpaAuditing
@SpringBootApplication
public class CafeAppApplication {

	public static void main(String[] args) {

		SpringApplication.run(CafeAppApplication.class, args);

		/**
		 * BCryptPasswordEncoder()값으로 암호화
		 */
		String rawPassword = "adminPassword";
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String encodedPassword = encoder.encode(rawPassword);
		System.out.println(encodedPassword);
	}

}
