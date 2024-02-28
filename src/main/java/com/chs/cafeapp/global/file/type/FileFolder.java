package com.chs.cafeapp.global.file.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FileFolder {

  DRINK("음료"),
  FOOD("음식"),
  GOODS("굿즈");

  private final String description;

  public static FileFolder fromDescription(String input) {
    for (FileFolder folder : FileFolder.values()) {
      if (folder.getDescription().equals(input)) {
        return folder;
      }
    }
    throw new IllegalArgumentException("No matching constant for [" + input + "]");
  }
}
