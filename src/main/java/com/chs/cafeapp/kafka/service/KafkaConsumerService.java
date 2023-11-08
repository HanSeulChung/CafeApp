package com.chs.cafeapp.kafka.service;

import com.chs.cafeapp.order.dto.OrderResponse;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

  @KafkaListener(topics = "order-status-topic", groupId = "orderstatus", containerFactory = "kafkaListener")
  public void consume(String orderResponse){
    String[] parts = orderResponse.split(":");
    if (parts.length == 2) {
      String orderId = parts[0];
      String message = parts[1].trim();
      System.out.println("order id = " + orderId);
      System.out.println("order message = " + message);
    } else {
      // 예외 처리: 메시지 형식이 잘못된 경우
      System.err.println("Invalid message format: " + orderResponse);
    }
  }
}
