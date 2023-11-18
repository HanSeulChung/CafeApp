package com.chs.cafeapp.auth.component;

import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class TokenBlackList {
  private Set<String> blacklist = new HashSet<>();

  public void addToBlacklist(String accessToken) {
    blacklist.add(accessToken);
  }

  public boolean isBlacklisted(String accessToken) {
    return blacklist.contains(accessToken);
  }
}
