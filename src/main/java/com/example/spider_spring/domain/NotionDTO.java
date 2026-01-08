package com.example.spider_spring.domain;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotionDTO {
	private String id;
    private String title; // 메모 내용
    private String date;  // 날짜
}