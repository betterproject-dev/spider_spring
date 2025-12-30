package com.example.spider_spring.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.spider_spring.domain.RejectionRates;

@Repository
public interface RejectionRateRepository extends JpaRepository<RejectionRates, Integer>{
	// 특정 호기의 최근 데이터 7개를 가져오는 쿼리 (이름을 확 줄였습니다)
    @Query(value = "SELECT * FROM rejection_rates WHERE machine_number = :machineId " +
                   "ORDER BY created_at DESC LIMIT 7", nativeQuery = true)
    List<RejectionRates> findRecent(@Param("machineId") Integer machineId);
}
