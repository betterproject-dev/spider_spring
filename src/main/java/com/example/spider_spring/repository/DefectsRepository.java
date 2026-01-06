package com.example.spider_spring.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.spider_spring.domain.DefectType;
import com.example.spider_spring.domain.Defects;
import com.example.spider_spring.domain.DefectsDTO;

@Repository
public interface DefectsRepository extends JpaRepository<Defects, Integer>{
	// 특정 시간 이후 전체 검사수
    @Query("SELECT COUNT(d) FROM Defects d WHERE d.machine.id = :id AND d.createdAt > :after")
    long countTotalAfter(@Param("id") Integer id, @Param("after") LocalDateTime after);

    // 특정 시간 이후 불량 검사수
    @Query("SELECT COUNT(d) FROM Defects d WHERE d.machine.id = :id AND d.createdAt > :after AND d.defectType != 'Normal'")
    long countRejectedAfter(@Param("id") Integer id, @Param("after") LocalDateTime after);

    // 그래프용 통계 ('Normal' 제외)
    @Query("SELECT new com.example.spider_spring.domain.DefectsDTO(d.defectType, COUNT(d)) " +
           "FROM Defects d " +
    	   "WHERE d.createdAt >= :startDate AND d.createdAt < :endDate " +
           "AND d.defectType != 'Normal' " +  // 불량 그래프에 'Nomal'제외
    	   "GROUP BY d.defectType")
    List<DefectsDTO> countDefectsByPeriod(@Param("startDate") LocalDateTime startDate,
    									  @Param("endDate") LocalDateTime endDate);
}
