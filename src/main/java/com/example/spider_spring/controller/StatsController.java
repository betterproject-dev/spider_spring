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
        
        // 데이터 가져오기 (Top 7 혹은 오늘 전체 등 기획에 따라 조절 가능)
        List<RejectionRates> rates = rejectionRateRepository.findRecent(machineId);
        
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM-dd");

        List<RejectionRatesDTO> response = rates.stream().map(r -> {
        	LocalDateTime ldt = r.getCreatedAt().toLocalDateTime();
        	// LocalDateTime 혹은 Timestamp에서 시간/날짜 추출
            // 오늘 데이터면 시간(HH:mm) 표시 (예: 14:30)
        	String date = "today".equals(type)
        		? ldt.format(timeFormatter)
                // 7일 데이터면 날짜(MM-dd) 표시 (예: 12-30)
                : ldt.format(dateFormatter);
            return new RejectionRatesDTO(date, r.getRejectionRate());
          })
          .sorted(Comparator.comparing(RejectionRatesDTO::getCreatedAt)) // 차트 표시를 위해 시간순 정렬
          .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}
