package com.chs.cafeapp.global.kafka.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationMessage {
  private String userId;
  private long orderId;
  private String orderStatus;
}