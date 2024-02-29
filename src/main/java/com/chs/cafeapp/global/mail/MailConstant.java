package com.chs.cafeapp.global.mail;

public class MailConstant {
  public static final String MAIL_DOMAIN = "http://localhost:8080";
  public static final String TO_ADMIN = "admins";
  public static final String TO_MEMBER = "members";
  public static final String MAIL_TITLE = "CafeApp 인증 메일";
  public static final String MAIL_TEXT_WITH_FRONT = "가입을 축하합니다. <br> 아래 6자리 숫자를 입력칸에 10분 이내 입력 후 인증을 완료하세요.<br>";
  public static final String MAIL_TEXT = "가입을 축하합니다. 아래 링크를 클릭하여서 가입을 완료하세요.<br>";
  public static final String MAIL_CERTIFICATION_GUIDE = "가입을 축하드립니다. 로그인 아이디로 메일을 확인해 인증을 완료한 뒤 서비스를 사용할 수 있습니다.";
  public static final String MAIL_CERTIFICATION_SUCCESS = "인증이 완료되었습니다. CafeApp 서비스를 이용 가능합니다.";
  public static final int EMAIL_VERIFICATION_LIMIT_IN_SECONDS = 600;
}
