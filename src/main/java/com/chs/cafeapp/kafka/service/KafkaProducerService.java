package com.chs.cafeapp.kafka.service;

import com.chs.cafeapp.order.dto.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {
  private static final String TOPIC = "order-status-topic";
  private final KafkaTemplate<String, String> kafkaTemplate;

  public void sendMessage(long orderId, String newStatus) {
    String message = orderId + ":" + newStatus;
    System.out.println("message = " + message);
    kafkaTemplate.send(TOPIC, message);

  }
}
