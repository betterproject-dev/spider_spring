package com.example.spider_spring.repository;

import java.time.LocalDateTime;

import org.springframework.stereotype.Repository;

@Repository
public interface HeartbeatRepository {
	void updateLastSeen(Integer machineId, LocalDateTime now);
	
	LocalDateTime findLastSeen(Integer machineId);
}
