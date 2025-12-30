package com.example.spider_spring.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.spider_spring.domain.SensorsDTO;
import com.example.spider_spring.service.SensorService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/sensors")
@RequiredArgsConstructor
public class SensorController {

	private final SensorService sensorService;
	
	
	@GetMapping("/day")
	public ResponseEntity<?> getDayData(@RequestParam(name = "date") String date, @RequestParam(name="machineNumber") Integer machineNumber) {
		
		try {
			List<SensorsDTO> dayresults = sensorService.getSensorDataByDay(date, machineNumber);
		
			if (dayresults.isEmpty()) {
				return ResponseEntity.noContent().build();
			}
			
			return ResponseEntity.ok(dayresults);
		} catch (Exception e) {
			return ResponseEntity.status(500).body("데이터 조회 중 오류 발생" + e.getMessage() );
		}
	}
	
	
	// 주간 통계 API
	@GetMapping("/week")
	public ResponseEntity<?> getWeekData(@RequestParam(name = "date") String date, @RequestParam(name="machineNumber") Integer machineNumber) {
	    try {
	        List<SensorsDTO> weekresults = sensorService.getSensorDataByWeek(machineNumber);
	        
			if (weekresults.isEmpty()) {
				return ResponseEntity.noContent().build();
			}
	        
	        
	        return ResponseEntity.ok(weekresults);
	        
	    } catch (Exception e) {
	        return ResponseEntity.status(500).body("주간 데이터 조회 중 오류 발생" + e.getMessage() );
	            
	    }
	}
	
}

	

