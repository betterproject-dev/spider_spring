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
	
	// 전체 알림 (7일)
    List<AlertEvent> findByStartedAtAfterOrderByStartedAtDesc(Timestamp since);

    // 완료 알림
    List<AlertEvent> findByEndedAtIsNotNullAndStartedAtAfterOrderByStartedAtDesc(Timestamp since);

    // 진행중 전체
    List<AlertEvent> findByEndedAtIsNullOrderByStartedAtDesc();

    // ACK 안한 EMERGENCY 최신 1건
    AlertEvent findTopByEndedAtIsNullAndLevelAndAcknowledgedAtIsNullOrderByIdDesc(AlertLevel level);

    // ================================
    //  RECHECK 대상
    // ================================
    @Query("""
      select e from AlertEvent e
      where e.endedAt is null
        and e.level = :level
        and e.acknowledgedAt is not null
        and e.acknowledgedAt <= :before
      order by e.acknowledgedAt asc
    """)
    List<AlertEvent> findRecheckTargets(
        @Param("level") AlertLevel level,
        @Param("before") Timestamp before
    );

    // ================================
    //  ACK
    // ================================
    @Modifying
    @Query("""
      update AlertEvent e
         set e.acknowledgedAt = :ts
       where e.id = :id
         and e.endedAt is null
         and e.acknowledgedAt is null
    """)
    int acknowledge(@Param("id") Integer id, @Param("ts") Timestamp ts);

    // ================================
    //  RESOLVE (active_key 반드시 NULL 처리)
    // ================================
    @Modifying
    @Query("""
      update AlertEvent e
         set e.endedAt = :ts,
             e.activeKey = null
       where e.id = :id
         and e.endedAt is null
    """)
    int resolve(@Param("id") Integer id, @Param("ts") Timestamp ts);

    // ================================
    //  active_key 기반 진행중 조회 (중복 방지 핵심)
    // ================================
    AlertEvent findTopByActiveKeyAndEndedAtIsNullOrderByStartedAtDesc(String activeKey);

    // fallback (기존)
    AlertEvent findTopByMachine_IdAndEndedAtIsNullAndLevelOrderByStartedAtDesc(Integer machineId, AlertLevel level);
}
