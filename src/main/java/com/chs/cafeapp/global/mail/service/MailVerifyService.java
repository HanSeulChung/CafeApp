package com.chs.cafeapp.global.mail.service;

import static com.chs.cafeapp.global.exception.type.ErrorCode.EMAIL_NOT_FOUND;
import static com.chs.cafeapp.global.exception.type.ErrorCode.INVALID_CERTIFIED_NUMBER;
import static com.chs.cafeapp.global.exception.type.ErrorCode.NOT_MATCH_CERTIFIED_NUMBER;

import com.chs.cafeapp.global.exception.CustomException;
import com.chs.cafeapp.global.redis.auth.CertifiedNumberAuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailVerifyService {
  private final CertifiedNumberAuthRepository certifiedNumberAuthRepository;

  public void verifyEmail(String email, String certificationNumber) {
    if (!isVerify(email, certificationNumber)) {
      throw new CustomException(INVALID_CERTIFIED_NUMBER);
    }
    certifiedNumberAuthRepository.removeCertificationNumber(email);
  }

  private boolean isVerify(String email, String certificationNumber) {
    boolean validatedEmail = isEmailExists(email);
    if (!validatedEmail) {
      throw new CustomException(EMAIL_NOT_FOUND);
    }

    String number = certifiedNumberAuthRepository.getCertificationNumber(email);
    boolean validateNumber = certificationNumber.equals(number);
    if (!validateNumber) {
      throw new CustomException(NOT_MATCH_CERTIFIED_NUMBER);
    }
    return validatedEmail && validateNumber;
  }

  private boolean isEmailExists(String email) {
    return certifiedNumberAuthRepository.hasKey(email);
  }
}
