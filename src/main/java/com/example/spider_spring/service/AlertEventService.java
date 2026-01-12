package com.example.spider_spring.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.spider_spring.domain.AlertEvent;
import com.example.spider_spring.domain.AlertEventDTO;
import com.example.spider_spring.domain.AlertLevel;
import com.example.spider_spring.repository.AlertEventRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlertEventService {
	
	private final AlertEventRepository alertEventRepository;
	
	// 숫자 상수
	private static final long ALERT_DAYS = 7;            // 전체/호기 알림 조회 범위(일)
	private static final long RECHECK_MINUTES = 10;      // ACK 후 재확인까지 대기(분)
	
	private static Timestamp nowUtcTs() {
	    return Timestamp.from(Instant.now()); // ✅ UTC
	}
	
	private static Timestamp sinceDays(long days) {
	   return Timestamp.from(Instant.now().minus(days, ChronoUnit.DAYS));
	}

	private static Timestamp beforeMinutes(long minutes) {
		return Timestamp.from(Instant.now().minus(minutes, ChronoUnit.MINUTES));
	}
	
	@Value("${spider.admin-pin}")
	private String adminPin;
	
	private String normalizePin(String s) {
		if (s == null) return null;
		// 앞뒤 공백 제거 후, 양끝이 따옴표면 제거
		s = s.trim();
		if((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'"))) {
			s = s.substring(1, s.length() - 1);
		}
		return s.trim();
	}
	
	private boolean isValidPin(String pin) {
		String p = normalizePin(pin);
		String a = normalizePin(adminPin);
		return p != null && a != null && p.equals(a);
	}
	
	@Transactional(readOnly = true)
	public List<AlertEventDTO> getAllAlerts() {
		// 전체 알림에서 일주일 이내 알림만 나오게 하는것
		Timestamp since = sinceDays(ALERT_DAYS);
		
		return alertEventRepository.findByStartedAtAfterOrderByStartedAtDesc(since)
				.stream()
				.map(AlertEventDTO::new)
				.toList();
	}
	
	@Transactional(readOnly = true)
	public List<AlertEventDTO> getActiveAlerts() {
		return alertEventRepository.findByEndedAtIsNullOrderByStartedAtDesc()
				.stream()
				.map(AlertEventDTO::new)
				.toList();
	}
	

	// 완료 상태
	@Transactional(readOnly = true)
	public List<AlertEventDTO> getResolvedAlertsLast7Days() {
	  Timestamp since = sinceDays(ALERT_DAYS);
	  return alertEventRepository
	      .findByEndedAtIsNotNullAndStartedAtAfterOrderByStartedAtDesc(since)
	      .stream().map(AlertEventDTO::new).toList();
	}
	
	// 모달 "확인" 버튼 -> ACK 저장
	/**
	   * 모달 "확인" 버튼 -> acknowledged_at 기록
	   * - update row 수가 1이면 성공
	   * - 0이면: 이미 종료/이미 ACK/없는 ID
	   */
	@Transactional
	public boolean acknowledge(Integer alertEventId) {
		int updated = alertEventRepository.acknowledge(alertEventId, nowUtcTs());
		if (updated > 0) return true;
		if (!alertEventRepository.existsById(alertEventId)) {
			throw new NoSuchElementException("AlertEvent not found: " + alertEventId);
		}
		return false;
	}
	
	// "정상 가동" 선택 -> 이벤트 종료
	/**
	   * "정상 가동" -> ended_at 기록 + active_key NULL (Repository에서 처리)
	   * - update row 수가 1이면 성공
	   * - 0이면: 이미 종료/없는 ID
	   */
	@Transactional
	public boolean resolve(Integer alertEventId) {
		int updated = alertEventRepository.resolve(alertEventId, nowUtcTs());
	    if (updated > 0) return true;
		if (!alertEventRepository.existsById(alertEventId)) {
			throw new NoSuchElementException("AlertEvent not found: " + alertEventId);
		}
		return false;
	}
	
	 /**
	   * PIN 포함 resolve
	   * - PIN 틀리면 INVALID_PIN throw
	   */
	 @Transactional
	 public boolean resolveWithPin(Integer alertEventId, String pin) {
	    if (!isValidPin(pin)) {
	      throw new IllegalArgumentException("INVALID_PIN");
	    }
	    return resolve(alertEventId);
	  }
	
	// 10분 후 확인창 띄울 때: 현재 이벤트가 아직 진행 중인지 확인
	@Transactional(readOnly = true)
	public AlertEventDTO getAlert(Integer alertEventId) {
		AlertEvent e = alertEventRepository.findById(alertEventId)
				.orElseThrow(() -> new NoSuchElementException("AlertEvent not found: " + alertEventId));
		return new AlertEventDTO(e);
	}
	
	/**
	   * 전역 긴급 모달용:
	   * 1) ACK 안 한 EMERGENCY 최신 1건 -> mode=ALERT
	   * 2) ACK 했고 10분 지난 EMERGENCY 중 가장 오래된 1건 -> mode=RECHECK
	   */
	@Transactional(readOnly = true)
	public AlertEventDTO getNextEmergencyForModal() {

	    AlertEvent unacked =
	        alertEventRepository
	            .findTopByEndedAtIsNullAndLevelAndAcknowledgedAtIsNullOrderByIdDesc(AlertLevel.EMERGENCY);

	    if (unacked != null) {
	      AlertEventDTO dto = new AlertEventDTO(unacked);
	      dto.setMode("ALERT");
	      return dto;
	    }

	    Timestamp before = beforeMinutes(RECHECK_MINUTES);
	    List<AlertEvent> rechecks =
	        alertEventRepository.findRecheckTargets(AlertLevel.EMERGENCY, before);

	    if (!rechecks.isEmpty()) {
	      AlertEventDTO dto = new AlertEventDTO(rechecks.get(0));
	      dto.setMode("RECHECK");
	      return dto;
	    }

	    return null;
	  }
	
	
	/**
	   * 특정 machine의 진행중 EMERGENCY 1건을 종료
	   * - (현재 방식) machine_id 기준으로 최신 진행중 EMERGENCY를 찾아 resolve
	   */
	@Transactional
	public boolean resolveActiveByMachine(Integer machineId) {
	    AlertEvent ongoing = alertEventRepository
	        .findTopByMachine_IdAndEndedAtIsNullAndLevelOrderByStartedAtDesc(machineId, AlertLevel.EMERGENCY);

	    if (ongoing == null) return false;
	    return alertEventRepository.resolve(ongoing.getId(), nowUtcTs()) > 0;
	}

	  /**
	   * (추천) active_key 기반으로 진행중을 찾고 싶으면 이런 메서드도 추가 가능
	   * - Flask에서 active_key를 "EMERGENCY:{machine_number}"로 넣는 구조와 맞춤
	   */
	@Transactional
	public boolean resolveActiveByMachineActiveKey(Integer machineNumber) {
	    String activeKey = "EMERGENCY:" + machineNumber;
	    AlertEvent ongoing = alertEventRepository.findTopByActiveKeyAndEndedAtIsNullOrderByStartedAtDesc(activeKey);
	    if (ongoing == null) return false;
	    return alertEventRepository.resolve(ongoing.getId(), nowUtcTs()) > 0;
	}
	

}
