package com.example.spider_spring.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	public void heartbeat(@RequestBody HeartbeatDTO req) {
		heartbeatService.update(req.getMachineId());
	}
	
	@GetMapping("/status/{machineId}")
	public ResponseEntity<Map<String, String>> status(@PathVariable Integer machineId) {

	    String status = heartbeatService.checkStatus(machineId);

	    return ResponseEntity.ok(
	        Map.of("status", status)
	    );
	}
	

}
