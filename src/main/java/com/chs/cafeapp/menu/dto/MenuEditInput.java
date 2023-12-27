package com.chs.cafeapp.menu.dto;

import com.chs.cafeapp.menu.validator.ValidFile;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
@AllArgsConstructor
public class MenuEditInput {

  @NotNull
  private Long id;
  @NotBlank
  private String name;
  @NotNull
  private int kcal;
  @NotBlank
  private String description;
  @NotNull
  private int stock;
  @NotNull
  private int price;
  @NotBlank
  private String status;
  @ValidFile
  private MultipartFile menuImageFileUrl;
  @NotBlank
  private String superCategory;
  @NotBlank
  private String baseCategory;
}
