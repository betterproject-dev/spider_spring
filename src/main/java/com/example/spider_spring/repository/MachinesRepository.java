package com.example.spider_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.spider_spring.domain.Machines;

@Repository
public interface MachinesRepository extends JpaRepository<Machines, Integer> {

}
