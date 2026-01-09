package com.example.spider_spring.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.spider_spring.domain.ApiResponse;
import com.example.spider_spring.domain.HeartbeatDTO;
import com.example.spider_spring.service.HeartbeatService;

@RestController
@RequestMapping("/api/heartbeat")
public class HeartbeatController {

	private final HeartbeatService heartbeatService;
	
	public HeartbeatController(HeartbeatService heartbeatService) {
		this.heartbeatService = heartbeatService;
	}
	
	@PostMapping
	public ApiResponse<Void> heartbeat(@RequestBody HeartbeatDTO req) {
		heartbeatService.update(req.getMachineId());
		// 특별한 데이터 반환이 없으므로 success(null) 반환
		return ApiResponse.success(null);
	}
	
	@GetMapping("/status/{machineId}")
	public ApiResponse<String> status(@PathVariable("machineId") Integer machineId) {

	    String status = heartbeatService.checkStatus(machineId);

	 // 데이터 필드에 바로 status 문자열을 담아서 반환
        return ApiResponse.success(status);
	}
	

}
