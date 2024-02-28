package com.chs.cafeapp.global.config.security;

import com.chs.cafeapp.auth.component.TokenPrepareList;
import com.chs.cafeapp.global.security.JwtAccessDeniedHandler;
import com.chs.cafeapp.global.security.JwtAuthenticationEntryPoint;
import com.chs.cafeapp.global.security.JwtAuthenticationFilter;
import com.chs.cafeapp.global.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final CorsFilter corsFilter;
    private final TokenProvider tokenProvider;
    private final TokenPrepareList tokenPrepareList;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private static final String[] AUTH_WHITELIST = {
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/v2/api-docs",
            "/webjars/**",
            "/menus/**",
            "/h2-console/**",
            "auth/members/sign-up",
            "auth/members/sign-in",
            "auth/members/email-auth",
            "auth/admins/sign-up",
            "auth/admins/sign-in",
            "auth/admins/email-auth"
    };

    private static final String[] AUTH_ADMINLIST = {
        "auth/admins/passwords"
    };

    private static final String[] AUTH_MEMBERLIST = {
        "/orders/**",
        "/carts/**",
        "/stamps/**",
        "/coupons/**",
        "auth/members/passwords",
        "auth/members/logout",
        "auth/admins/passwords",
        "auth/admins/logout"
    };
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(AUTH_WHITELIST);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .addFilterBefore(new JwtAuthenticationFilter(tokenProvider, tokenPrepareList), UsernamePasswordAuthenticationFilter.class)
            .httpBasic().disable()
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .exceptionHandling()
            .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            .accessDeniedHandler(jwtAccessDeniedHandler)
            .and()
            .authorizeRequests()
            .antMatchers(AUTH_WHITELIST).permitAll()
            .antMatchers(AUTH_ADMINLIST).hasAuthority("ROLE_ADMIN")
            .antMatchers(AUTH_MEMBERLIST).hasAuthority("ROLE_USER")
            .and()
            .apply(new JwtSecurityConfig(tokenProvider, tokenPrepareList));
    }
}
