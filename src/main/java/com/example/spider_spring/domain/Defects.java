package com.example.spider_spring.domain;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "defects")
public class Defects {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
		
	@Column(name = "created_at", insertable = false, updatable = false)	
	@CreationTimestamp
	private Timestamp createdAt;
	
	@Column(name = "defect_type")
	@Enumerated(EnumType.STRING)
	private DefectType defectType;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "machine_number")
	private Machines machine;
}
