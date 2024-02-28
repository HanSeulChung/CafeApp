package com.chs.cafeapp.domain.stamp.controller;

import com.chs.cafeapp.domain.stamp.service.StampService;
import com.chs.cafeapp.domain.stamp.dto.StampDto;
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
  public ResponseEntity<StampDto> viewStamp(@RequestParam String memberId) {
    return ResponseEntity.ok(stampService.viewStamp(memberId));
  }

}
