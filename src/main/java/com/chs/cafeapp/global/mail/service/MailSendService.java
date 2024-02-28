package com.chs.cafeapp.global.mail.service;

import static com.chs.cafeapp.global.mail.MailConstant.MAIL_DOMAIN;
import static com.chs.cafeapp.global.mail.MailConstant.MAIL_TEXT;
import static com.chs.cafeapp.global.mail.MailConstant.MAIL_TITLE;
import static com.chs.cafeapp.global.mail.MailConstant.TO_ADMIN;

import com.chs.cafeapp.global.mail.certifiednumber.CertifiedNumberGenerator;
import com.chs.cafeapp.global.redis.CertifiedNumberAuthRepository;
import java.security.NoSuchAlgorithmException;
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

  public void certifiedNumberMail(String email, String to) throws NoSuchAlgorithmException, MessagingException {
    String certificationNumber = numberGenerator.createCertificationNumber(6);
    certifiedNumberAuthRepository.saveCertificationNumber(email, certificationNumber);
    String subject = MAIL_TITLE;
    String link = String.format("<a href='%s/auth/%s/email-auth?id=%s&certifiedNumber=%s'> 이메일 인증 </a>", MAIL_DOMAIN, to, email, certificationNumber);
    String text = MAIL_TEXT + link;
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
}
