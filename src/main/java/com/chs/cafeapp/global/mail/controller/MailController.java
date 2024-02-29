package com.chs.cafeapp.global.mail.controller;


import com.chs.cafeapp.global.mail.dto.EmailRequest;
import com.chs.cafeapp.global.mail.service.MailSendService;
import java.security.NoSuchAlgorithmException;
import javax.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mail")
public class MailController {
  private final MailSendService mailSendService;

  @PostMapping()
  public ResponseEntity<Boolean> sendAuthMail(@RequestBody EmailRequest emailRequest) throws NoSuchAlgorithmException, MessagingException {
    return ResponseEntity.ok(mailSendService.certifiedNumberMailWithFront(emailRequest.getEmail(), emailRequest.getUserType()));
  }

}
