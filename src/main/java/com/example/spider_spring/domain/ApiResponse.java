package com.example.spider_spring.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {
	private boolean success;
	private T data;
	private String message;
	
	// 성공 응답 정적 팩토리 메서드
	public static <T> ApiResponse<T> success(T data) {
		return new ApiResponse<>(true, data, null);
	}
	
	// 실패 응답 정적 팩토리 메서드
	public static <T> ApiResponse<T> error(String message) {
		return new ApiResponse<>(false, null, message);
	}
	
}
