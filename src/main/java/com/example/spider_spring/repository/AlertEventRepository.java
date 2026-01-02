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
    
    // 이벤트 완료
    List<AlertEvent> findByEndedAtIsNotNullAndStartedAtAfterOrderByStartedAtDesc(Timestamp since);
    
    // 진행 중(STOP) 알림
    List<AlertEvent> findByEndedAtIsNullOrderByStartedAtDesc();
    
    AlertEvent findTopByEndedAtIsNullAndLevelAndAcknowledgedAtIsNullOrderByIdDesc(AlertLevel level);
    
    // acknowledged_at 찍기
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update AlertEvent a set a.acknowledgedAt = :ts where a.id = :id and a.acknowledgedAt is null")
    int acknowledge(@Param("id") Integer id, @Param("ts") Timestamp ts);
    
    // 종료 처리: ended_at 찍기 (이미 종료됐으면 무시), resolve = 문제 해결 처리
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update AlertEvent a set a.endedAt = :ts where a.id = :id and a.endedAt is null")
    int resolve(@Param("id") Integer id, @Param("ts") Timestamp ts);
    
    
    @Query("""
    		  SELECT a FROM AlertEvent a
    		  WHERE a.endedAt IS NULL
    		    AND a.level = :level
    		    AND a.acknowledgedAt IS NOT NULL
    		    AND a.acknowledgedAt <= :before
    		  ORDER BY a.acknowledgedAt ASC
    		""")
   List<AlertEvent> findRecheckTargets( @Param("level") AlertLevel level,@Param("before") Timestamp before);
    
   AlertEvent findTopByMachine_IdAndEndedAtIsNullOrderByStartedAtDesc(Integer machineId);
   AlertEvent findTopByMachine_IdAndEndedAtIsNullAndLevelOrderByStartedAtDesc(Integer machineId, AlertLevel level);
}
