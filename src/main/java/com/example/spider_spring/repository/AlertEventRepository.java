package com.example.spider_spring.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.spider_spring.domain.AlertEvent;

@Repository
public interface AlertEventRepository extends JpaRepository<AlertEvent, Integer>{
	List<AlertEvent> findByMachine_IdOrderByStartedAtDesc(Integer machineId);
}
