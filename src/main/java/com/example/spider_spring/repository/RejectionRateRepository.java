package com.example.spider_spring.repository;

import java.time.LocalDateTime;
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
	@Query(value = "SELECT DATE(created_at) as created_at, " +
            "AVG(rejection_rate) as rejection_rate, " +
            "SUM(total_inspected) as total_inspected, " +
            "SUM(total_rejected) as total_rejected " +
            "FROM rejection_rates " +
            "WHERE machine_number = :machineId AND created_at < CURDATE() " +
            "GROUP BY DATE(created_at) " +
            "ORDER BY created_at ASC LIMIT 7", nativeQuery = true)
	List<Object[]> findLast7Days(@Param("machineId") Integer machineId);
	
	// 특정 머신의 특정 시간 이후 데이터 한 건 찾기
    // 같은 분(Minute)에 이미 데이터가 있는지 확인하여 덮어쓰기 위해 사용합니다.
    @Query("SELECT r FROM RejectionRates r WHERE r.machine.id = :machineId AND r.createdAt >= :after")
    java.util.Optional<RejectionRates> findByMachineIdAndCreatedAtAfter(
            @Param("machineId") Integer machineId, 
            @Param("after") LocalDateTime after);
}
