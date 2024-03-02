package com.chs.cafeapp.global.security;

import static com.chs.cafeapp.auth.token.constant.TokenConstant.ACCESS_TOKEN_EXPIRE_TIME;
import static com.chs.cafeapp.auth.token.constant.TokenConstant.AUTHORITIES_KEY;
import static com.chs.cafeapp.auth.token.constant.TokenConstant.BEARER_TYPE;
import static com.chs.cafeapp.auth.token.constant.TokenConstant.REFRESH_TOKEN_EXPIRE_TIME;
import static com.chs.cafeapp.global.exception.type.ErrorCode.NO_ROLE_TOKEN;
import static com.chs.cafeapp.global.security.JwtAuthenticationFilter.BEARER_PREFIX;

import com.chs.cafeapp.auth.token.dto.TokenDto;
import com.chs.cafeapp.global.exception.CustomException;
import com.chs.cafeapp.global.redis.token.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenProvider {

  private final TokenRepository tokenRepository;
  private static final SecretKey key =  Keys.secretKeyFor(SignatureAlgorithm.HS512);

  public String generateAccessToken(String userId, Authentication authentication) {
    String authorities = authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.joining(","));


    long now = (new Date()).getTime();

    return Jwts.builder()
        .setSubject(userId)       // payload "sub": "name"
        .claim(AUTHORITIES_KEY, authorities)
        .setIssuedAt(new Date(now))      // payload "iat" : "현재 시간
        .setExpiration(new Date(now + ACCESS_TOKEN_EXPIRE_TIME))        // payload "exp": 151621022 (ex)
        .signWith(key, SignatureAlgorithm.HS512)    // header "alg": "HS512"
        .compact();
  }

  public String generateRefreshToken(String userId, Authentication authentication) {
    String authorities = authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.joining(","));

    long now = (new Date()).getTime();

    return Jwts.builder()
        .setSubject(userId)
        .claim(AUTHORITIES_KEY, authorities)
        .setIssuedAt(new Date(now))
        .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
        .signWith(key, SignatureAlgorithm.HS512)
        .compact();
  }

  public TokenDto saveTokenDto(Authentication authentication) {

    String accessToken = generateAccessToken(authentication.getName(), authentication);
    String refreshToken = generateRefreshToken(authentication.getName(), authentication);

    tokenRepository.saveAccessToken(authentication.getName(), accessToken);
    tokenRepository.saveRefreshToken(authentication.getName(), refreshToken);

    return TokenDto.builder()
        .grantType(BEARER_TYPE)
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();
  }

  public Authentication getAuthentication(String token) {

    if (token.startsWith(BEARER_PREFIX)) {
      token = token.substring(BEARER_PREFIX.length());
    }
    // 토큰 복호화
    Claims claims = parseClaims(token);

    if (claims.get(AUTHORITIES_KEY) == null) {
      throw new CustomException(NO_ROLE_TOKEN);
    }

    // 클레임에서 권한 정보 가져오기
    Collection<? extends GrantedAuthority> authorities =
        Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());

    // UserDetails 객체를 만들어서 Authentication 리턴
    UserDetails principal = new User(claims.getSubject(), "", authorities);

    return new UsernamePasswordAuthenticationToken(principal, "", authorities);
  }
  public String getUsername(String token) {
    return this.parseClaims(token).getSubject();
  }

  public boolean validateToken(String token) {
    Claims claims = parseClaims(token);
    return !claims.getExpiration().before(new Date());
  }

  public boolean checkInvalidToken(String userId, String token) {
    return tokenRepository.checkInValidAccessToken(userId, token);
  }
  private Claims parseClaims(String token) {
    try {
      return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    } catch (ExpiredJwtException e) {
      log.error("만료된 JWT 토큰입니다.");
      throw new CustomException(NO_ROLE_TOKEN);
    } catch (SignatureException | SecurityException | MalformedJwtException e) {
      log.error("잘못된 JWT 서명입니다.");
      throw new CustomException(NO_ROLE_TOKEN);
    } catch (UnsupportedJwtException e) {
      log.error("지원되지 않는 JWT 토큰입니다.");
      throw new CustomException(NO_ROLE_TOKEN);
    } catch (IllegalArgumentException e) {
      log.error("JWT 토큰이 잘못되었습니다.");
      throw new CustomException(NO_ROLE_TOKEN);
    }
  }
}
