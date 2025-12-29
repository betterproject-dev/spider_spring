package com.example.spider_spring.domain;

import java.security.Timestamp;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "defects")
public class Defects {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
		
	@Column(name = "created_at", insertable = false, updatable = false)	
	@CreationTimestamp
	private Timestamp createdAt;
	
	@Column(name = "defect_type")
	private String defectType;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "machine_number")
	private Machines machine;
}
