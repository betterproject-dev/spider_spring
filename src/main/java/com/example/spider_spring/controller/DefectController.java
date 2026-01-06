package com.example.spider_spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.spider_spring.domain.Defects;
import com.example.spider_spring.service.DefectService;

@RestController
@RequestMapping("/api/defects")
public class DefectController {
	@Autowired
    private DefectService defectService;

    // Flask에서 불량 감지 시 이 주소(POST)로 데이터를 보냄
    @PostMapping("/save")
    public String saveDefect(@RequestBody Defects defect) {
        System.out.println("LOG: Flask로부터 데이터 수신 -> " + defect.getDefectType());
        
        // DefectService를 호출하여 저장 + 통계 갱신을 동시에 수행
        defectService.saveDefect(defect);
        
        return "SUCCESS";
    }
}
