package com.example.spider_spring.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
	
	// 특정 호기 알림 전체 (최근 7일) 
	@GetMapping("/machine/{machineId}")
	public List<AlertEventDTO> getByMachine(@PathVariable Integer machineId) {
		return alertEventService.getAlertsByMachineLast7Days(machineId);
	}
	
	// 특정 호기 진행중 알림
	@GetMapping("/machine/{machineId}/active")
	public List<AlertEventDTO> getActiveByMachine(@PathVariable Integer machineId) {
		return alertEventService.getActiveAlertsByMachine(machineId);
	}
	
	// 알림 1건 상세(10분 후 확인창에서 사용)
	@GetMapping("/{id:\\d+}")
    public AlertEventDTO getOne(@PathVariable Integer id) {
		return alertEventService.getAlert(id);
	}
	
	// 모달 "확인" 버튼 -> acknowledged_at 기록
	@PostMapping("/{id}/ack")
    public ResponseEntity<?> acknowledge(@PathVariable Integer id) {
		boolean ok = alertEventService.acknowledge(id);
		return ResponseEntity.ok(Map.of("ok", ok));
	}
	
	// "정상 가동" 선택 -> ended_at 기록 (이벤트 종료)
	 @PostMapping("/{id}/resolve")
	    public ResponseEntity<?> resolve(@PathVariable Integer id) {
		 boolean ok = alertEventService.resolve(id);
		 return ResponseEntity.ok(Map.of("ok", ok));
	 }
	 
	 @GetMapping("/resolved")
	 public List<AlertEventDTO> getResolved() {
	   return alertEventService.getResolvedAlertsLast7Days();
	 }
	
}
