package com.example.spider_spring.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DefectsDTO {
	private Long labelCount;
	private Long crushedCount;
	private Long discoloredCount;
	private Long weightCount;
}
