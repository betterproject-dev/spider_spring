package com.example.spider_spring.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.spider_spring.domain.DefectType;
import com.example.spider_spring.domain.Machines;
import com.example.spider_spring.domain.RejectionRates;
import com.example.spider_spring.repository.DefectsRepository;
import com.example.spider_spring.repository.RejectionRateRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatsService {

	@Autowired private DefectsRepository defectsRepository;
	@Autowired private RejectionRateRepository rejectionRateRepository;

	@Scheduled(fixedRate = 60000) // 1분 임시 설정
	@Transactional
	public void updateMachineStats() {
		Integer machineId = 1; // 예시로 1호기 설정
		LocalDateTime ago = LocalDateTime.now().minusMinutes(1);
		
		// 최근 1분 동안의 데이터만 집계
		long total = defectsRepository.countTotalAfter(machineId, ago);
		long rejected = defectsRepository.countRejectedAfter(machineId, DefectType.Label, ago);
		
		// 최근 1분간 검사 데이터가 0개라면 저장하지 않고 종료 (카메라 미작동 혹은 제품 통과 안 함)
		if (total == 0) {
			System.out.println("LOG: 최근 1분간 검사 데이터가 없어 통계를 생성하지 않습니다.");
			return;
		}
		
		// 데이터가 있을 때만 불량률 계산
		double rate = ((double) rejected / total) * 100;
		rate = Math.round(rate * 100) / 100.0; // 소수점 둘째자리 반올림
		
		// 엔티티 생성 및 저장
		RejectionRates stats = new RejectionRates();
		
		// Machine 객체 매핑 (연관관계 설정)
		Machines machine = new Machines();
		machine.setId(machineId);
		
		stats.setMachine(machine);
		stats.setTotalInspected((int) total);
		stats.setTotalRejected((int) rejected);
		stats.setRejectionRate(rate);
		
		rejectionRateRepository.save(stats);
		
		System.out.println("LOG: " + machineId + "호기 불량률 업데이트 완료 -> " + rate + "%");
	}
}
