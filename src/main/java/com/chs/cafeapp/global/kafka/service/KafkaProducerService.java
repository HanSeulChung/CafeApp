package com.chs.cafeapp.global.kafka.service;

import com.chs.cafeapp.global.kafka.notification.dto.NotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {
  private static final String TOPIC = "order-status-topic";
  private final KafkaTemplate<String, NotificationMessage> kafkaTemplate;

  public void sendMessage(String userId, long orderId, String newStatus) {
    NotificationMessage notificationMessage = new NotificationMessage(userId, orderId, newStatus);
    log.info("주문 상태 변경 알림 전송. userId: {}, orderId : {}, newStatus : {}", userId, orderId, newStatus);
    kafkaTemplate.send(TOPIC, notificationMessage);
  }
}
