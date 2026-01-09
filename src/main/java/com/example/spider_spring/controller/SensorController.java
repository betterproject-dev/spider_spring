package com.example.spider_spring.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.spider_spring.domain.ApiResponse;
import com.example.spider_spring.domain.SensorsDTO;
import com.example.spider_spring.service.SensorService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/sensors")
@RequiredArgsConstructor
public class SensorController {

	private final SensorService sensorService;
	
	
	@GetMapping("/day")
	public ApiResponse<List<SensorsDTO>> getDayData(@RequestParam(name = "date") String date, @RequestParam(name="machineNumber") Integer machineNumber) {
		
		try {
			List<SensorsDTO> dayresults = sensorService.getSensorDataByDay(date, machineNumber);
		
			// 데이터가 비어있어도 null이 아닌 빈 리스트 []를 반환하여 
            // 프론트엔드에서 .map() 등을 안전하게 쓸 수 있게 합니다.
            return ApiResponse.success(dayresults);
		} catch (Exception e) {
			return ApiResponse.error("데이터 조회 중 오류 발생: " + e.getMessage());
		}
	}
	
	
	// 주간 통계 API
	@GetMapping("/week")
	public ApiResponse<List<SensorsDTO>> getWeekData(@RequestParam(name = "date") String date, @RequestParam(name="machineNumber") Integer machineNumber) {
	    try {
	        List<SensorsDTO> weekresults = sensorService.getSensorDataByWeek(machineNumber);
	        
	        return ApiResponse.success(weekresults);
	        
	    } catch (Exception e) {
	    	return ApiResponse.error("주간 데이터 조회 중 오류 발생: " + e.getMessage());
	            
	    }
	}
	
}

	

