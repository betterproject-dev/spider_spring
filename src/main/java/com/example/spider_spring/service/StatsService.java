package com.example.spider_spring.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.spider_spring.domain.DefectsLogDTO;
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

	@Transactional
	public void updateMachineStats(Integer machineId) {
		System.out.println("LOG: 메서드 진입 성공");
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime startOfMinute = now.withSecond(0).withNano(0); // 현재 분의 시작 시점
		
		// 해당 분의 전체 데이터 다시 집계 (방금 들어온 데이터 포함)
		long total = defectsRepository.countTotalAfter(machineId, startOfMinute);
		long rejected = defectsRepository.countRejectedAfter(machineId, startOfMinute);
		
		// 검사 데이터가 0개라면 저장하지 않고 종료 (카메라 미작동 혹은 제품 통과 안 함)
		if (total == 0)	return;
		
		// 데이터가 있을 때만 불량률 계산
		double rate = ((double) rejected / total) * 100;
		rate = Math.round(rate * 100) / 100.0; // 소수점 둘째자리 반올림
		
		// 같은 분(Minute)에 이미 저장된 통계가 있는지 확인 (업데이트 혹은 새로 생성)
		// 1분에 데이터가 10개 들어와도 row는 1개만 유지
		RejectionRates stats = rejectionRateRepository.findByMachineIdAndCreatedAtAfter(machineId, startOfMinute)
				.orElse(new RejectionRates());
		
		// Machine 객체 매핑 (연관관계 설정)
		Machines machine = new Machines();
		machine.setId(machineId);
		
		stats.setMachine(machine);
		stats.setTotalInspected((int) total);
		stats.setTotalRejected((int) rejected);
		stats.setRejectionRate(rate);
		
		rejectionRateRepository.save(stats);
		
		System.out.println("LOG: 실시간 데이터 유입으로 통계 갱신 완료 -> " + rate + "%");
	}

	// 로그 조회
	public List<DefectsLogDTO> getMachineStats(Integer machineId) {
		List<DefectsLogDTO> logList = defectsRepository.findLogByMachineId(machineId);
		return logList;
	}
}
