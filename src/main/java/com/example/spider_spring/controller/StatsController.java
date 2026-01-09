package com.example.spider_spring.controller;

import java.util.List;
import com.example.spider_spring.service.StatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.spider_spring.domain.RejectionRatesDTO;
import com.example.spider_spring.domain.ApiResponse;
import com.example.spider_spring.domain.DefectsDTO;
import com.example.spider_spring.domain.DefectsLogDTO;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final StatsService statsService;

    StatsController(StatsService statsService) {
        this.statsService = statsService;
    }
	
	// 막대 그래프용 (불량 유형별 통계)
	@GetMapping("/defect-summary")
	public ApiResponse<DefectsDTO> getSummary(
			@RequestParam(value = "machineId") Integer machineId,
			@RequestParam(value = "type", defaultValue = "today") String type) {
		
		return ApiResponse.success(statsService.getDefectSummary(machineId, type));
	}
	
	// 선 그래프용 (불량률 추이)
	@GetMapping("/rejection-trend/{machineId}")
    public ApiResponse<List<RejectionRatesDTO>> getTrend(
            @PathVariable("machineId") Integer machineId,
            @RequestParam(value = "type", defaultValue = "today") String type) {
		System.out.println("LOG: 요청 들어옴! 머신ID: " + machineId + ", 타입: " + type);
		
		return ApiResponse.success(statsService.getRejectionTrend(machineId, type));
    }
	
	// Flask로부터의 갱신 신호 (void 성격의 요청)
	@GetMapping("/update/{machineId}")
	public ApiResponse<Void> forceUpdateStats(@PathVariable("machineId") Integer machineId) {
	    System.out.println("LOG: Flask로부터 통계 갱신 신호 수신! 머신ID: " + machineId);
	    
	    // 서비스의 통계 업데이트 로직 호출
	    statsService.updateMachineStats(machineId);
	    
	    return ApiResponse.success(null, "Stats Updated Successfully");
	}
	
	// 통계 로그 조회
	@GetMapping("/getLog/{machineId}")
	public ApiResponse<List<DefectsLogDTO>> getLog(@PathVariable("machineId") Integer machineId) {
		return ApiResponse.success(statsService.getMachineStats(machineId));
	}
}
