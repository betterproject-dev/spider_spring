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
	
	private Boolean label;
	private Boolean crushed;
	private Boolean discolored;
	private Boolean weight;
	
	@Column(name = "image_url")
	private String imageUrl;
	
	@Column(name = "is_defect")
	private Boolean isDefect;
		
	@Column(name = "created_at", insertable = false, updatable = false)	
	@CreationTimestamp
	private Timestamp createdAt;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "machine_number")
	private Machines machine;
}
