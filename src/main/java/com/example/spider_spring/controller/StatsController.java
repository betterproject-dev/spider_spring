package com.example.spider_spring.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

	@Autowired
	private DefectsRepository defectsRepository;
	@Autowired
	private RejectionRateRepository rejectionRateRepository;
	
	// 막대 그래프용 (불량 유형별 통계)
	@GetMapping("/defect-summary")
	public ResponseEntity<List<DefectsDTO>> getSummary() {
		return ResponseEntity.ok(defectsRepository.countDefectsByType());
	}
	
	// 선 그래프용 (불량률 추이)
	@GetMapping("/rejection-trend/{machineId}")
    public ResponseEntity<List<RejectionRatesDTO>> getTrend(
            @PathVariable("machineId") Integer machineId,
            @RequestParam(value = "type", defaultValue = "today") String type) {
		System.out.println("LOG: 요청 들어옴! 머신ID: " + machineId + ", 타입: " + type);
        
		List<RejectionRates> rates;
	    DateTimeFormatter formatter;

	    if ("today".equals(type)) {
	        // 오늘 데이터 조회
	        rates = rejectionRateRepository.findTodayRates(machineId);
	        formatter = DateTimeFormatter.ofPattern("HH:mm");
	    } else {
	        // 7일 데이터 조회 (데이터가 거꾸로 나오지 않게 여기서 다시 정렬)
	        rates = rejectionRateRepository.findLast7Days(machineId);
	        rates.sort(Comparator.comparing(RejectionRates::getCreatedAt)); // 시간순 재정렬
	        formatter = DateTimeFormatter.ofPattern("MM-dd");
	    }

	    List<RejectionRatesDTO> response = rates.stream().map(r -> {
	        String label = r.getCreatedAt().toLocalDateTime().format(formatter);
	        return new RejectionRatesDTO(label, r.getRejectionRate());
	    }).collect(Collectors.toList());

	    return ResponseEntity.ok(response);
    }
}
