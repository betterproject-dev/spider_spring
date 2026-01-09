package com.example.spider_spring.controller;

import com.example.spider_spring.domain.ApiResponse;
import com.example.spider_spring.domain.NotionDTO;
import com.example.spider_spring.service.NotionService;
import lombok.RequiredArgsConstructor;
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
    public ApiResponse<List<NotionDTO>> getMemos() {
        try {
            List<NotionDTO> results = notionService.getAllMemos();
            
            // 데이터가 비어있어도 구조(success: true, data: [])를 유지하는 것이 프론트 처리에 더 유리.
            return ApiResponse.success(results);
        } catch (Exception e) {
        	// 에러 발생 시 공통 에러 응답
            return ApiResponse.error("데이터 조회 중 오류 발생: " + e.getMessage());
        }
    }
    
    // ✅ 메모 저장 API
    @PostMapping("/memo")
    public ApiResponse<Void> saveMemo(@RequestBody NotionDTO dto) {
        try {
            notionService.saveToNotion(dto);
         // 문자열 "success" 대신 성공 객체와 메시지 반환
            return ApiResponse.success(null, "메모가 성공적으로 저장되었습니다.");
        } catch (Exception e) {
        	return ApiResponse.error("노션 저장 오류: " + e.getMessage());
        }
    }
    // ✅ 삭제 API 추가
    @DeleteMapping("/memo/{id}")
    public ApiResponse<Void> deleteMemo(@PathVariable("id") String id) {
        try {
            notionService.deleteMemo(id);
            return ApiResponse.success(null, "메모가 삭제되었습니다.");
        } catch (Exception e) {
        	e.printStackTrace();
            return ApiResponse.error("삭제 실패: " + e.getMessage());
        }
    }


}