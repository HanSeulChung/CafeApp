package com.chs.cafeapp.global.mail.service;

import static com.chs.cafeapp.auth.type.UserType.ADMIN;
import static com.chs.cafeapp.auth.type.UserType.MEMBER;
import static com.chs.cafeapp.global.mail.MailConstant.MAIL_DOMAIN;
import static com.chs.cafeapp.global.mail.MailConstant.MAIL_TEXT;
import static com.chs.cafeapp.global.mail.MailConstant.MAIL_TEXT_WITH_FRONT;
import static com.chs.cafeapp.global.mail.MailConstant.MAIL_TITLE;
import static com.chs.cafeapp.global.mail.MailConstant.TO_ADMIN;
import static com.chs.cafeapp.global.mail.MailConstant.TO_MEMBER;

import com.chs.cafeapp.auth.type.UserType;
import com.chs.cafeapp.global.mail.certifiednumber.CertifiedNumberGenerator;
import com.chs.cafeapp.global.redis.CertifiedNumberAuthRepository;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class MailSendService {

  private final JavaMailSender javaMailSender;
  private final CertifiedNumberGenerator numberGenerator;
  private final CertifiedNumberAuthRepository certifiedNumberAuthRepository;

  /**
   * 프론트가 존재할때의 로직 구현
   * @param email
   * @param userType
   * @return
   * @throws NoSuchAlgorithmException
   * @throws MessagingException
   */
  public Boolean certifiedNumberMailWithFront(String email, UserType userType) throws NoSuchAlgorithmException, MessagingException {
    String to = "";
    if (userType == ADMIN) {
      to = TO_ADMIN;
    }
    if (userType == MEMBER) {
      to = TO_MEMBER;
    }

    String certificationNumber = numberGenerator.createCertificationNumber(6);
    certifiedNumberAuthRepository.saveCertificationNumber(email, certificationNumber);
    String subject = MAIL_TITLE;
    String expiredTimeMention = String.format("<br> 이 인증 번호는 %s 까지 유효합니다.", mailExpiredTime());
    String text = MAIL_TEXT_WITH_FRONT + certificationNumber + expiredTimeMention;
    sendMail(email, subject, text);

    return Boolean.TRUE;
  }

  /**
   * 프론트가 존재하지 않을 때 메일 인증 전송 과정
   * @param email
   * @param to
   * @throws NoSuchAlgorithmException
   * @throws MessagingException
   */
  public void certifiedNumberMail(String email, String to) throws NoSuchAlgorithmException, MessagingException {
    String certificationNumber = numberGenerator.createCertificationNumber(6);
    String subject = MAIL_TITLE;
    String link = String.format("<a href='%s/auth/%s/email-auth?id=%s&certifiedNumber=%s'> 이메일 인증 </a>", MAIL_DOMAIN, to, email, certificationNumber);
    String expiredTimeMention = String.format("<br> 이 인증 링크는 %s 까지 유효합니다.", mailExpiredTime());
    String text = MAIL_TEXT + link + expiredTimeMention;
    certifiedNumberAuthRepository.saveCertificationNumber(email, certificationNumber);
    sendMail(email, subject, text);
  }
  @Async
  public void sendMail(String to, String title, String content) throws MessagingException{
    MimeMessage message = javaMailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message);
    helper.setTo(to);
    helper.setSubject(title);
    helper.setText(content, true);

    javaMailSender.send(message);
  }

  private String mailExpiredTime() {
    LocalDateTime currentTime = LocalDateTime.now();
    LocalDateTime expiredTime = currentTime.plusMinutes(10);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    return expiredTime.format(formatter);
  }
}
