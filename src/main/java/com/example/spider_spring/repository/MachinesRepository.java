package com.example.spider_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.spider_spring.domain.Machines;

public interface MachinesRepository extends JpaRepository<Machines, Integer> {

}
