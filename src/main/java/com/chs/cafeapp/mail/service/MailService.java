package com.chs.cafeapp.mail.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class MailService {

  private final JavaMailSender javaMailSender;

  public MailService(JavaMailSender javaMailSender) {
    this.javaMailSender = javaMailSender;
  }

  @Async
  public void sendMail(String to, String subject, String content) {
    MimeMessage message = javaMailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message);

    try {
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(content, true);
    } catch (MessagingException e) {
      e.printStackTrace(); // 예외 처리 로직 추가 필요
    }

    javaMailSender.send(message);
  }
}
