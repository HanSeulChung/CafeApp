package com.chs.cafeapp.menu.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class ValidFileValidator implements ConstraintValidator<ValidFile, MultipartFile> {

  @Override
  public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
    return file != null && !file.isEmpty();
  }
}
