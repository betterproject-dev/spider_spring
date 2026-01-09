package com.example.spider_spring.controller;

import org.springframework.web.bind.annotation.*;

import com.example.spider_spring.domain.ApiResponse;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminController {

	@PostMapping("/check-admin")
	public ApiResponse<Void> checkAdmin(@RequestBody Map<String, String> request) {
		String inputNumber = request.get("number");
		
		if ("7777".equals(inputNumber)) {
			// 성공 응답: 데이터는 null, 성공 메시지 포함
			return ApiResponse.success(null, "관리자 인증 성공!");
		} else {
			// 실패 응답: ApiResponse.error 메서드 사용
			return ApiResponse.error("번호가 일치하지 않습니다.");
		}
		
	}
}
