package com.chs.cafeapp.global.config.security;

import com.chs.cafeapp.auth.component.TokenPrepareList;
import com.chs.cafeapp.global.security.JwtAuthenticationFilter;
import com.chs.cafeapp.global.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class JwtSecurityConfig extends
    SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
  private final TokenProvider tokenProvider;
  private final TokenPrepareList tokenPrepareList;

  // TokenProvider 를 주입받아서 JwtFilter 를 통해 Security 로직에 필터를 등록
  @Override
  public void configure(HttpSecurity http) {
    JwtAuthenticationFilter customFilter = new JwtAuthenticationFilter(tokenProvider, tokenPrepareList);
    http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
  }
}
