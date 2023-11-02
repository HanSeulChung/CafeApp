package com.chs.cafeapp.stamp.controller;

import com.chs.cafeapp.stamp.dto.StampDto;
import com.chs.cafeapp.stamp.service.StampService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 스탬프 Controller
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/stamps")
public class StampController {
  private final StampService stampService;

  @GetMapping
  public ResponseEntity<StampDto> viewStamp(@RequestParam String userId) {
    return ResponseEntity.ok(stampService.viewStamp(userId));
  }

}
