package com.example.spider_spring.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.spider_spring.domain.AlertEvent;
import com.example.spider_spring.domain.AlertLevel;

@Repository
public interface AlertEventRepository extends JpaRepository<AlertEvent, Integer>{
	
	// 전체 알림 7일 이내
    List<AlertEvent> findByStartedAtAfterOrderByStartedAtDesc(Timestamp since);
    
    // 특정 호기 알림 전체 7일 이내
    List<AlertEvent> findByMachine_IdAndStartedAtAfterOrderByStartedAtDesc(Integer machineId, Timestamp since);
    
    // 특정 호기 + 진행 중(STOP)
    List<AlertEvent> findByMachine_IdAndEndedAtIsNullOrderByStartedAtDesc(Integer machineId);

    // 특정 호기 + 레벨
    List<AlertEvent> findByMachine_IdAndLevelOrderByStartedAtDesc(
        Integer machineId, AlertLevel level
    );
    
    // 진행 중(STOP) 알림
    List<AlertEvent> findByEndedAtIsNullOrderByStartedAtDesc();
    
    // 진행 중 + 특정 레벨 (최신순)  -> 전역 긴급 모달용
    List<AlertEvent> findByEndedAtIsNullAndLevelOrderByStartedAtDesc(AlertLevel level);
    
    // acknowledged_at 찍기
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update AlertEvent a set a.acknowledgedAt = CURRENT_TIMESTAMP " + 
    		"where a.id = :id and a.acknowledgedAt is null")
    int acknowledge(@Param("id") Integer id);
    
    // 종료 처리: ended_at 찍기 (이미 종료됐으면 무시), resolve = 문제 해결 처리
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update AlertEvent a set a.endedAt = CURRENT_TIMESTAMP " +
    		"where a.id = :id and a.endedAt is null")
    int resolve(@Param("id") Integer id);
    
    @Query("""
    		  SELECT a FROM AlertEvent a
    		  WHERE a.endedAt IS NULL
    		    AND a.level = :level
    		    AND a.acknowledgedAt IS NOT NULL
    		    AND a.acknowledgedAt <= :before
    		  ORDER BY a.acknowledgedAt ASC
    		""")
   List<AlertEvent> findRecheckTargets( @Param("level") AlertLevel level,@Param("before") Timestamp before);
}
