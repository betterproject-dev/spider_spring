package com.example.spider_spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.spider_spring.domain.Defects;
import com.example.spider_spring.repository.DefectsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DefectService {
	@Autowired private DefectsRepository defectsRepository;
	@Autowired private StatsService statsService;
	
	@Transactional
	public void saveDefect(Defects defect) {
		// 불량 데이터 저장
		defectsRepository.save(defect);
		// 즉시 통계 계산 및 RejectionRates 생성/갱신 호출
		statsService.updateMachineStats(defect.getMachine().getId());
		
	}
}
