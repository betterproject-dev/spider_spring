package com.example.spider_spring.controller;

import com.example.spider_spring.domain.NotionDTO;
import com.example.spider_spring.service.NotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "http://localhost:5173", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PATCH, RequestMethod.DELETE})
@RestController
@RequestMapping("/api/notion")
@RequiredArgsConstructor
public class NotionController {

    private final NotionService notionService;

    
    // ✅ 메모 전체 조회 API
    @GetMapping("/memos")
    public ResponseEntity<?> getMemos() {
        try {
            List<NotionDTO> results = notionService.getAllMemos();
            
            if (results.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("데이터 조회 중 오류 발생: " + e.getMessage());
        }
    }
    
    // ✅ 메모 저장 API
    @PostMapping("/memo")
    public ResponseEntity<?> saveMemo(@RequestBody NotionDTO dto) {
        try {
            notionService.saveToNotion(dto);
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("노션 저장 오류: " + e.getMessage());
        }
    }
    // ✅ 삭제 API 추가
    @DeleteMapping("/memo/{id}")
    public ResponseEntity<?> deleteMemo(@PathVariable("id") String id) {
        try {
            notionService.deleteMemo(id);
            return ResponseEntity.ok("deleted");
        } catch (Exception e) {
        	e.printStackTrace();
            return ResponseEntity.status(500).body("삭제 실패: " + e.getMessage());
        }
    }


}