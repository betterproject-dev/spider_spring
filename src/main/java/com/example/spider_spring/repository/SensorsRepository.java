package com.example.spider_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.spider_spring.domain.Sensors;

public interface SensorsRepository extends JpaRepository<Sensors, Integer> {

}
