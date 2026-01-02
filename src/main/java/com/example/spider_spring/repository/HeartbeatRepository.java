package com.example.spider_spring.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.spider_spring.domain.Heartbeat;

@Repository
public interface HeartbeatRepository extends JpaRepository<Heartbeat, Integer> {

	Optional<Heartbeat> findByMachineId(Integer machineId);
}
