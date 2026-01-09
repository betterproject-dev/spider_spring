package com.example.spider_spring.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.spider_spring.domain.DefectsDTO;
import com.example.spider_spring.domain.DefectsLogDTO;
import com.example.spider_spring.domain.Machines;
import com.example.spider_spring.domain.RejectionRates;
import com.example.spider_spring.domain.RejectionRatesDTO;
import com.example.spider_spring.repository.DefectsRepository;
import com.example.spider_spring.repository.RejectionRateRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatsService {

	@Autowired private DefectsRepository defectsRepository;
	@Autowired private RejectionRateRepository rejectionRateRepository;
	
	public DefectsDTO getDefectSummary(Integer machineId, String type) {
		LocalDateTime start = calculateStartTime(type);
		return defectsRepository.getDefectCounts(machineId, start);
	}
	
	public List<RejectionRatesDTO> getRejectionTrend(Integer machineId, String type) {
		if ("today".equals(type)) {
	        List<RejectionRates> rates = rejectionRateRepository.findTodayRates(machineId);
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
	        
	        return rates.stream().map(r -> 
	            new RejectionRatesDTO(
	                r.getCreatedAt().toLocalDateTime().format(formatter), 
	                r.getRejectionRate(), 
	                r.getTotalInspected(), 
	                r.getTotalRejected()
	            )
	        ).collect(Collectors.toList());
	    } else {
	        // 7일 데이터 조회 (Object[] 배열 처리)
	        List<Object[]> rawData = rejectionRateRepository.findLast7Days(machineId);
	        
	        return rawData.stream().map(obj -> {
	        	// obj[1]은 AVG(rejection_rate) 결과값이므로 소수점이 길 수 있음
	        	double rawRate = ((Number) obj[1]).doubleValue();
	        	// 소수점 둘째 자리까지 반올림 처리
	        	double formattedRate = Math.round(rawRate * 100.0) / 100.0;
	        	
	            // obj[0]: 날짜(Date), obj[1]: 평균불량률, obj[2]: 총검사수, obj[3]: 총불량수
	            return new RejectionRatesDTO(
	                obj[0].toString(), // "2026-01-06" 형태
	                formattedRate,
	                ((Number) obj[2]).intValue(),
	                ((Number) obj[3]).intValue()
	            );
	        }).collect(Collectors.toList());
	    }
	}
	
	private LocalDateTime calculateStartTime(String type) {
		if("7days".equalsIgnoreCase(type) || "week".equalsIgnoreCase(type)) {
			// 현재로부터 7일 전 00:00부터
			return LocalDateTime.now().minusDays(7).withHour(0).withMinute(0).withSecond(0).withNano(0);
		}
		// 오늘 00:00부터
		return LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
	}

	@Transactional
	public void updateMachineStats(Integer machineId) {
		System.out.println("LOG: 메서드 진입 성공");
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime startOfMinute = now.withSecond(0).withNano(0); // 현재 분의 시작 시점
		
		// 해당 분의 전체 데이터 다시 집계 (방금 들어온 데이터 포함)
		long total = defectsRepository.countTotalAfter(machineId, startOfMinute);
		long rejected = defectsRepository.countRejectedAfter(machineId, startOfMinute);
		
		// 검사 데이터가 0개라면 저장하지 않고 종료 (카메라 미작동 혹은 제품 통과 안 함)
		if (total == 0)	return;
		
		// 데이터가 있을 때만 불량률 계산
		double rate = ((double) rejected / total) * 100;
		rate = Math.round(rate * 100) / 100.0; // 소수점 둘째자리 반올림
		
		// 같은 분(Minute)에 이미 저장된 통계가 있는지 확인 (업데이트 혹은 새로 생성)
		// 1분에 데이터가 10개 들어와도 row는 1개만 유지
		RejectionRates stats = rejectionRateRepository.findByMachineIdAndCreatedAtAfter(machineId, startOfMinute)
				.orElse(new RejectionRates());
		
		// Machine 객체 매핑 (연관관계 설정)
		Machines machine = new Machines();
		machine.setId(machineId);
		
		stats.setMachine(machine);
		stats.setTotalInspected((int) total);
		stats.setTotalRejected((int) rejected);
		stats.setRejectionRate(rate);
		
		rejectionRateRepository.save(stats);
		
		System.out.println("LOG: 실시간 데이터 유입으로 통계 갱신 완료 -> " + rate + "%");
	}

	// 로그 조회
	public List<DefectsLogDTO> getMachineStats(Integer machineId) {
		List<DefectsLogDTO> logList = defectsRepository.findLogByMachineId(machineId);

		logList.removeIf(log -> !log.getCrushed()&&!log.getDiscolored()&&!log.getLabel()&&!log.getWeight());
		
		return logList;
	}
}
