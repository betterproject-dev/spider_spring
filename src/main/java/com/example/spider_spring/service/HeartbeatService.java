package com.example.spider_spring.service;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.example.spider_spring.repository.HeartbeatRepository;

@Service
public class HeartbeatService {
	
	private final HeartbeatRepository repo;
	
	public HeartbeatService(HeartbeatRepository repo) {
		this.repo = repo;
	}
	
	public void update(Integer machineId) {
		repo.updateLastSeen(machineId, LocalDateTime.now());
	}
	
	public String checkStatus(Integer machinId) {
		LocalDateTime lastSeen = repo.findLastSeen(machinId);
		
		if (lastSeen == null) return "OFFLINE";
		
		if (Duration.between(lastSeen, LocalDateTime.now()).getSeconds() > 30) {
			return "OFFLINE";
		}
		return "ONLINE";
	}
}
