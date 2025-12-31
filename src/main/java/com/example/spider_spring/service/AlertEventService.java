package com.example.spider_spring.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;

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
	
	private static Timestamp sinceDays(long days) {
	   return Timestamp.from(Instant.now().minus(days, ChronoUnit.DAYS));
	}

	private static Timestamp beforeMinutes(long minutes) {
		return Timestamp.from(Instant.now().minus(minutes, ChronoUnit.MINUTES));
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
	
	// 몇 호기 전체 알림
	@Transactional(readOnly = true)
	public List<AlertEventDTO> getAlertsByMachineLast7Days(Integer machineId) {
	    Timestamp since = sinceDays(ALERT_DAYS);;
	    return alertEventRepository
	            .findByMachine_IdAndStartedAtAfterOrderByStartedAtDesc(machineId, since)
	            .stream().map(AlertEventDTO::new).toList();
	}
	
	// 몇 호기 현재 STOP 상태
	@Transactional(readOnly = true)
	public List<AlertEventDTO> getActiveAlertsByMachine(Integer machineId) {
		return alertEventRepository
				.findByMachine_IdAndEndedAtIsNullOrderByStartedAtDesc(machineId)
				.stream()
				.map(AlertEventDTO::new)
				.toList();
	}
	
	// 모달 "확인" 버튼 -> ACK 저장
	@Transactional
	public boolean acknowledge(Integer alertEventId) {
		if (!alertEventRepository.existsById(alertEventId)) {
			throw new NoSuchElementException("AlertEvent not found: " + alertEventId);
		}
		return alertEventRepository.acknowledge(alertEventId) > 0;
	}
	
	// "정상 가동" 선택 -> 이벤트 종료
	@Transactional
	public boolean resolve(Integer alertEventId) {
		if (!alertEventRepository.existsById(alertEventId)) {
			throw new NoSuchElementException("AlertEvent not found: " + alertEventId);
		}
		return alertEventRepository.resolve(alertEventId) > 0;
	}
	
	// 10분 후 확인창 띄울 때: 현재 이벤트가 아직 진행 중인지 확인
	@Transactional(readOnly = true)
	public AlertEventDTO getAlert(Integer alertEventId) {
		AlertEvent e = alertEventRepository.findById(alertEventId)
				.orElseThrow(() -> new NoSuchElementException("AlertEvent not found: " + alertEventId));
		return new AlertEventDTO(e);
	}
	
	@Transactional(readOnly = true)
	public AlertEventDTO getNextEmergencyForModal() {

		 // ACK 안 한 EMERGENCY 최신 1건 (id desc)
	    AlertEvent unacked =
	        alertEventRepository
	            .findTopByEndedAtIsNullAndLevelAndAcknowledgedAtIsNullOrderByIdDesc(AlertLevel.EMERGENCY);

	    if (unacked != null) {
	        AlertEventDTO dto = new AlertEventDTO(unacked);
	        dto.setMode("ALERT");
	        return dto;
	    }

	    //  ACK 했고 + 10분 지난 것 중 가장 오래된 것 (recheck용)
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

}
