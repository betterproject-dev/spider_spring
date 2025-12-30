package com.example.spider_spring.domain;

import java.sql.Timestamp;

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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rejection_rates")
public class RejectionRates {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "created_at")
	@CreationTimestamp
	private Timestamp createdAt;
	
	@Column(name = "rejection_rate")
	private Double rejectionRate;  // 불량률(%)
	
	@Column(name = "total_inspected")
	private Integer totalInspected; // 총 검사 개수
	
	@Column(name = "total_rejected")
	private Integer totalRejected; // 총 불량 개수
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "machine_number")
	private Machines machine;
}
