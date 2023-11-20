package com.chs.cafeapp.auth.component;

import static com.chs.cafeapp.auth.token.type.ACCESS_TOKEN_TYPE.NO_ACCESS_TOKEN;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class TokenPrepareList {
  private Map<String, String> spareList = new HashMap<>();

  public void addToSpareList(String loginId, String accessToken) {
    spareList.put(loginId, accessToken);
  }

  public String getAccessToken(String loginId) {
    return spareList.getOrDefault(loginId, NO_ACCESS_TOKEN.getToken_value());
  }

  public void delete(String loginId) {
    spareList.remove(loginId);
  }
}
