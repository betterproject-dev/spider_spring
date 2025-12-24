package com.example.spider_spring.controller;

import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminController {

	@PostMapping("/check-admin")
	public Map<String, Object> checkAdmin(@RequestBody Map<String, String> request) {
		String inputNumber = request.get("number");
		Map<String, Object> response = new HashMap<>();
		
		if ("7777".equals(inputNumber)) {
			response.put("success", true);
			response.put("message", "관리자 인증 성공!");
		} else {
			response.put("success", false);
			response.put("message", "번호가 일치하지 않습니다.");
		}
		return response;
	}
}
