package com.example.spider_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.spider_spring.domain.Sensors;

@Repository
public interface SensorsRepository extends JpaRepository<Sensors, Integer> {

}
