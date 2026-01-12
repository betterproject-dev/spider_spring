package com.example.spider_spring.controller;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.spider_spring.domain.AlertEventDTO;
import com.example.spider_spring.domain.ApiResponse;
import com.example.spider_spring.service.AlertEventService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/alerts")
public class AlertEventController {
	
	private final AlertEventService alertEventService;
	
	// 전체 알림 (최근 7일) - 서비스에서 필터 처리
	@GetMapping
	public ApiResponse<List<AlertEventDTO>> getAll() {
		return ApiResponse.success(alertEventService.getAllAlerts());
	}
	
	// 진행중 알림 전체(ended_at == null)
	@GetMapping("/active")
	public ApiResponse<List<AlertEventDTO>> getActive() {
		return ApiResponse.success(alertEventService.getActiveAlerts());
	}
	
	// 진행 중 EMERGENCY 알림 (전역 긴급 모달용)
	@GetMapping("/active/emergency")
	public ResponseEntity<ApiResponse<AlertEventDTO>> getNextEmergency() {
		AlertEventDTO dto = alertEventService.getNextEmergencyForModal();
		if (dto == null ) {
			return ResponseEntity.ok(ApiResponse.success(null));
		}
	    return ResponseEntity.ok(ApiResponse.success(dto));
	}
	
	// 알림 1건 상세(10분 후 확인창에서 사용)
	@GetMapping("/{id:\\d+}") // 숫자인 id만 이 메서드로 받겠다라는 정규식 라우팅, id는 숫자만 허용 1자리 이상
    public ResponseEntity<ApiResponse<AlertEventDTO>> getOne(@PathVariable("id") Integer id) {
		try {
		      return ResponseEntity.ok(ApiResponse.success(alertEventService.getAlert(id)));
		} catch (NoSuchElementException e) {
		      return ResponseEntity.status(404).body(ApiResponse.error("해당 알림을 찾을 수 없습니다."));
		}
	}
	
	// 모달 "확인" 버튼 -> acknowledged_at 기록
	@PostMapping("/{id}/ack")
    public ResponseEntity<ApiResponse<Boolean>> acknowledge(@PathVariable("id") Integer id) {
		try {
		    boolean ok = alertEventService.acknowledge(id);

		      if (!ok) {
		        // update 0건: 이미 ended 되었거나 이미 ack된 상태
		        return ResponseEntity.badRequest().body(ApiResponse.error("이미 처리된 알림입니다."));
		      }

		    return ResponseEntity.ok(ApiResponse.success(true, "알림이 확인되었습니다."));
		} catch (NoSuchElementException e) {
		    return ResponseEntity.status(404).body(ApiResponse.error("해당 알림을 찾을 수 없습니다."));
		}
	}
	
	// "정상 가동" 선택 -> ended_at 기록 (이벤트 종료)
	 @PostMapping("/{id}/resolve")
	    public ResponseEntity<ApiResponse<Boolean>> resolve(@PathVariable("id") Integer id, @RequestBody Map<String, String> body) {
		 String pin = (body == null) ? null : body.get("pin");
		 
		 try {
			 boolean ok = alertEventService.resolveWithPin(id, pin);
			 if (!ok) {
			        // update 0건: 이미 ended 상태
			        return ResponseEntity.badRequest().body(ApiResponse.error("이미 종료된 알림입니다."));
			 }
			 return ResponseEntity.ok(ApiResponse.success(true, "정상 가동으로 처리되었습니다."));
		 } catch (IllegalArgumentException e) {
			// 에러 상황에 맞는 메시지를 ApiResponse.error에 담아서 전달
		    String errorMsg = "INVALID_PIN".equals(e.getMessage()) 
		         ? "관리자 PIN이 올바르지 않습니다." 
		         : "요청이 올바르지 않습니다.";
		    return ResponseEntity.badRequest().body(ApiResponse.error(errorMsg));
		 } catch (NoSuchElementException e) {
			 return ResponseEntity.status(404).body(ApiResponse.error("해당 알림을 찾을 수 없습니다."));
		 }
	 }
	 
	 @GetMapping("/resolved")
	 public ApiResponse<List<AlertEventDTO>> getResolved() {
	   return ApiResponse.success(alertEventService.getResolvedAlertsLast7Days());
	 }

}
