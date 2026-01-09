package com.example.spider_spring.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.spider_spring.domain.AlertEventDTO;
import com.example.spider_spring.service.AlertEventService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/alerts")
public class AlertEventController {
	
	private final AlertEventService alertEventService;
	
	// 전체 알림 (최근 7일) - 서비스에서 필터 처리
	@GetMapping
	public List<AlertEventDTO> getAll() {
		return alertEventService.getAllAlerts();
	}
	
	// 진행중 알림 전체(ended_at == null)
	@GetMapping("/active")
	public List<AlertEventDTO> getActive() {
		return alertEventService.getActiveAlerts();
	}
	
	// 진행 중 EMERGENCY 알림 (전역 긴급 모달용)
	@GetMapping("/active/emergency")
	public ResponseEntity<AlertEventDTO> getNextEmergency() {
		AlertEventDTO dto = alertEventService.getNextEmergencyForModal();
		if (dto == null ) return ResponseEntity.noContent().build();
	    return ResponseEntity.ok(dto);
	}
	
	// 알림 1건 상세(10분 후 확인창에서 사용)
	@GetMapping("/{id:\\d+}") // 숫자인 id만 이 메서드로 받겠다라는 정규식 라우팅, id는 숫자만 허용 1자리 이상
    public AlertEventDTO getOne(@PathVariable("id") Integer id) {
		return alertEventService.getAlert(id);
	}
	
	// 모달 "확인" 버튼 -> acknowledged_at 기록
	@PostMapping("/{id}/ack")
    public ResponseEntity<?> acknowledge(@PathVariable("id") Integer id) {
		boolean ok = alertEventService.acknowledge(id);
		return ResponseEntity.ok(Map.of("ok", ok));
	}
	
	// "정상 가동" 선택 -> ended_at 기록 (이벤트 종료)
	 @PostMapping("/{id}/resolve")
	    public ResponseEntity<?> resolve(@PathVariable("id") Integer id, @RequestBody Map<String, String> body) {
		 String pin = body.get("pin");
		 
		 try {
			 boolean ok = alertEventService.resolveWithPin(id, pin);
			 return ResponseEntity.ok(Map.of("ok", ok));
		 } catch (IllegalArgumentException e) {
			 if("INVALID_PIN".equals(e.getMessage())) {
				 return ResponseEntity.badRequest().body(
				   Map.of("ok", false, "message", "관리자 PIN이 올바르지 않습니다.")		 
				);
			 }
			 return ResponseEntity.badRequest().body(
			    Map.of("ok", false, "message", "요청이 올바르지 않습니다.")		 
			);
		 }
	 }
	 
	 @GetMapping("/resolved")
	 public List<AlertEventDTO> getResolved() {
	   return alertEventService.getResolvedAlertsLast7Days();
	 }

}
