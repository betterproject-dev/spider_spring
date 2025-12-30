package com.example.spider_spring.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.spider_spring.domain.RejectionRates;

@Repository
public interface RejectionRateRepository extends JpaRepository<RejectionRates, Integer>{
	// 1. 오늘 데이터만 가져오기 (시간순 정렬)
	@Query(value = "SELECT * FROM rejection_rates WHERE machine_number = :machineId " +
            "AND created_at >= CURDATE() " +
            "AND created_at < DATE_ADD(CURDATE(), INTERVAL 1 DAY) " +
            "ORDER BY created_at ASC", nativeQuery = true)
	List<RejectionRates> findTodayRates(@Param("machineId") Integer machineId);

    // 2. 직전 7일 평균 데이터 가져오기 (최신순 7개)
	@Query(value = "SELECT * FROM rejection_rates WHERE machine_number = :machineId " +
			"AND created_at < CURDATE() " + // 오늘 이전 데이터만
            "ORDER BY created_at DESC LIMIT 7", nativeQuery = true)
	List<RejectionRates> findLast7Days(@Param("machineId") Integer machineId);
}
