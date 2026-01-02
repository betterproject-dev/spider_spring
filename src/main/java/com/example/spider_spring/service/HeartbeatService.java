package com.example.spider_spring.service;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.example.spider_spring.domain.Heartbeat;
import com.example.spider_spring.repository.HeartbeatRepository;

@Service
public class HeartbeatService {

    private final HeartbeatRepository repo;

    public HeartbeatService(HeartbeatRepository repo) {
        this.repo = repo;
    }

    // 🔹 라즈베리파이 → 생존 신호
    public void update(Integer machineId) {

        Heartbeat heartbeat = repo.findById(machineId)
            .orElseGet(() -> {
                Heartbeat hb = new Heartbeat();
                hb.setMachineId(machineId);
                hb.setLastSeen(LocalDateTime.now());
                return hb;
            });

        heartbeat.updateNow();
        repo.save(heartbeat);
    }

    // 🔹 프론트 / 관제 → 상태 확인
    public String checkStatus(Integer machineId) {

        return repo.findById(machineId)
            .map(hb -> {
                if (Duration.between(hb.getLastSeen(), LocalDateTime.now()).getSeconds() > 30) {
                    return "OFFLINE";
                }
                return "ONLINE";
            })
            .orElse("OFFLINE");
    }
}
