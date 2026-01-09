package com.example.spider_spring.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DefectsLogDTO {
	private Integer id;
	
	private Boolean label;
	private Boolean crushed;
	private Boolean discolored;
	private Boolean weight;
	
	private String imageUrl;
    private Timestamp createdAt;
}