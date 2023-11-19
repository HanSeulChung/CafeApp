package com.chs.cafeapp.kafka.service;

import com.chs.cafeapp.kafka.notification.dto.NotificationMessage;
import com.chs.cafeapp.kafka.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {
  private final NotificationService notificationService;
  private final String TOPIC_NAME = "order-status-topic";
  private final String GROUP_ID = "orderstatus";
  @KafkaListener(topics = TOPIC_NAME, groupId = GROUP_ID, containerFactory = "kafkaListener")
  public void consume(NotificationMessage notificationMessage){
    if (notificationMessage != null) {
      String userId = notificationMessage.getUserId();
      long orderId = notificationMessage.getOrderId();
      String orderStatus = notificationMessage.getOrderStatus();

      // 사용자 유효성 검사 및 login 유무
      notificationService.sendNotificationToUser(userId);

      // TODO: FCM을 통해 알람 구현

    } else {
      log.error("NotificationMessage가 null 값 입니다.");
    }
  }
}
