package com.example.spider_spring.repository;

import java.util.List;

//import java.sql.Timestamp;
//import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.spider_spring.domain.Sensors;

@Repository
public interface SensorsRepository extends JpaRepository<Sensors, Integer> {

//	List<Sensors> findByMachineIdAndCreatedAtBetween(Integer machineId, Timestamp start, Timestamp end);
	
    @Query(value = "SELECT * FROM sensors " +
            "WHERE machine_number = :machineNumber " +
            "AND DATE(created_at) = :date " +
            "ORDER BY created_at", 
    nativeQuery = true)
    List<Sensors> findByMachineNumberAndDate(
    		@Param("machineNumber") Integer machineNumber,
    		@Param("date") String date
);
}

