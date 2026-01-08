package com.example.spider_spring.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import com.example.spider_spring.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.spider_spring.domain.RejectionRatesDTO;
import com.example.spider_spring.domain.DefectsDTO;
import com.example.spider_spring.domain.RejectionRates;
import com.example.spider_spring.repository.DefectsRepository;
import com.example.spider_spring.repository.RejectionRateRepository;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final StatsService statsService;

	@Autowired
	private DefectsRepository defectsRepository;
	@Autowired
	private RejectionRateRepository rejectionRateRepository;

    StatsController(StatsService statsService) {
        this.statsService = statsService;
    }
	
	// 막대 그래프용 (불량 유형별 통계)
	@GetMapping("/defect-summary")
	public ResponseEntity<DefectsDTO> getSummary(
			@RequestParam(value = "machineId") Integer machineId,
			@RequestParam(value = "type", defaultValue = "today") String type) {
		
		LocalDateTime start;
		
		if("7days".equalsIgnoreCase(type) || "week".equalsIgnoreCase(type)) {
			// 현재로부터 7일 전 00:00부터
			start = LocalDateTime.now().minusDays(7).withHour(0).withMinute(0).withSecond(0).withNano(0);
		} else {
			// 오늘 00:00부터
			start = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
		}
		
		return ResponseEntity.ok(defectsRepository.getDefectCounts(machineId, start));
	}
	
	// 선 그래프용 (불량률 추이)
	@GetMapping("/rejection-trend/{machineId}")
    public ResponseEntity<List<RejectionRatesDTO>> getTrend(
            @PathVariable("machineId") Integer machineId,
            @RequestParam(value = "type", defaultValue = "today") String type) {
		System.out.println("LOG: 요청 들어옴! 머신ID: " + machineId + ", 타입: " + type);


	    if ("today".equals(type)) {
	        List<RejectionRates> rates = rejectionRateRepository.findTodayRates(machineId);
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
	        
	        List<RejectionRatesDTO> response = rates.stream().map(r -> 
	            new RejectionRatesDTO(
	                r.getCreatedAt().toLocalDateTime().format(formatter), 
	                r.getRejectionRate(), 
	                r.getTotalInspected(), 
	                r.getTotalRejected()
	            )
	        ).collect(Collectors.toList());
	        return ResponseEntity.ok(response);

	    } else {
	        // 7일 데이터 조회 (Object[] 배열 처리)
	        List<Object[]> rawData = rejectionRateRepository.findLast7Days(machineId);
	        
	        List<RejectionRatesDTO> response = rawData.stream().map(obj -> {
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
	        
	        return ResponseEntity.ok(response);
	    }
    }
	
	
	@GetMapping("/update/{machineId}")
	public ResponseEntity<String> forceUpdateStats(@PathVariable("machineId") Integer machineId) {
	    System.out.println("LOG: Flask로부터 통계 갱신 신호 수신! 머신ID: " + machineId);
	    
	    // 서비스의 통계 업데이트 로직 호출
	    statsService.updateMachineStats(machineId);
	    
	    return ResponseEntity.ok("Stats Updated Successfully");
	}
}
