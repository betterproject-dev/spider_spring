package com.example.spider_spring.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.spider_spring.domain.Defects;
import com.example.spider_spring.domain.DefectsDTO;
import com.example.spider_spring.domain.DefectsLogDTO;

@Repository
public interface DefectsRepository extends JpaRepository<Defects, Integer>{
	// 특정 시간 이후 전체 검사수
    @Query("SELECT COUNT(d) FROM Defects d WHERE d.machine.id = :id AND d.createdAt > :after")
    long countTotalAfter(@Param("id") Integer id, @Param("after") LocalDateTime after);

    // 특정 시간 이후 불량 검사수
    @Query("SELECT COUNT(d) FROM Defects d WHERE d.machine.id = :id AND d.createdAt > :after AND d.isDefect = true")
    long countRejectedAfter(@Param("id") Integer id, @Param("after") LocalDateTime after);

    // 그래프용 통계
    @Query("SELECT new com.example.spider_spring.domain.DefectsDTO(" +
    		"SUM(CASE WHEN d.label = true THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN d.crushed = true THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN d.discolored = true THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN d.weight = true THEN 1 ELSE 0 END)) " +
            "FROM Defects d WHERE d.machine.id = :machineId AND d.createdAt >= :startDate")
     DefectsDTO getDefectCounts(@Param("machineId") Integer machineId, @Param("startDate") LocalDateTime startDate);

    @Query("SELECT new com.example.spider_spring.domain.DefectsLogDTO(" +
            "d.id, d.label, d.crushed, d.discolored, d.weight, d.imageUrl, d.createdAt) " +
            "FROM Defects d WHERE d.machine.id = :machineId ORDER BY d.createdAt DESC")
    List<DefectsLogDTO> findLogByMachineId(@Param("machineId") Integer machineId);
}
