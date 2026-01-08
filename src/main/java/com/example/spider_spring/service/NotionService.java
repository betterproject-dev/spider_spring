package com.example.spider_spring.service;
import com.example.spider_spring.domain.NotionDTO;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;


@Service
public class NotionService {
    private final String NOTION_TOKEN = ""; 
    private final String DATABASE_ID = "";    

    // ✅ 노션에 메모 저장
    public void saveToNotion(NotionDTO dto) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.notion.com/v1/pages";

        HttpHeaders headers = createHeaders();
        
        Map<String, Object> body = new HashMap<>();
        body.put("parent", Map.of("database_id", DATABASE_ID));
        
        Map<String, Object> properties = new HashMap<>();
        properties.put("내용", Map.of("title", List.of(Map.of("text", Map.of("content", dto.getTitle())))));
        
        String dateWithTime = dto.getDate();
        if (!dto.getDate().contains("T")) {
            java.time.ZoneId korea = java.time.ZoneId.of("Asia/Seoul");
            java.time.ZonedDateTime nowKST = java.time.ZonedDateTime.now(korea);

            // ✅ 날짜는 프론트에서 받은 것 사용, 시간만 현재 시간
            java.time.LocalTime timeKST = nowKST.toLocalTime();
            String timeStr = timeKST.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));

            // ✅ 타임존 정보 포함 (중요!)
            dateWithTime = dto.getDate() + "T" + timeStr + "+09:00";
        
        }
        properties.put("날짜", Map.of("date", Map.of("start", dateWithTime)));
       
        body.put("properties", properties);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        restTemplate.postForEntity(url, entity, String.class);
    }
    


	    private RestTemplate getPatchSupportedRestTemplate() {
	        HttpClient httpClient = HttpClients.createDefault();
	        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
	        
	        // 스프링 부트 3.x 이상 환경에 맞는 타임아웃 설정
	        requestFactory.setConnectionRequestTimeout(5000); 
	        
	        return new RestTemplate(requestFactory);
	    }
	
	    // ✅ 메모 삭제 기능 (PATCH 요청)
	    public void deleteMemo(String id) {
	        RestTemplate restTemplate = getPatchSupportedRestTemplate();
	        String url = "https://api.notion.com/v1/pages/" + id;
	
	        HttpHeaders headers = createHeaders();
	        Map<String, Object> body = new HashMap<>();
	        body.put("archived", true);
	
	        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
	        restTemplate.exchange(url, HttpMethod.PATCH, entity, String.class);
	    }
    // ✅ 조회 로직 (ID 포함하도록 수정)
    @SuppressWarnings("unchecked")
    public List<NotionDTO> getAllMemos() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.notion.com/v1/databases/" + DATABASE_ID + "/query";
        HttpHeaders headers = createHeaders();
        
        // 최신순 정렬 바디 적용
        String jsonBody = "{\"sorts\": [{\"property\": \"날짜\", \"direction\": \"descending\"}]}";
        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
        
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            List<Map<String, Object>> results = (List<Map<String, Object>>) response.getBody().get("results");
            return parseNotionData(results);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @SuppressWarnings("unchecked")
    private List<NotionDTO> parseNotionData(List<Map<String, Object>> results) {
        List<NotionDTO> memoList = new ArrayList<>();
        for (Map<String, Object> row : results) {
            try {
                // ✅ 1. 각 행의 고유 ID 추출
                String id = row.get("id").toString(); 
                
                Map<String, Object> props = (Map<String, Object>) row.get("properties");
                String title = ((List<Map<String, Object>>) ((Map<String, Object>) props.get("내용")).get("title"))
                                .get(0).get("plain_text").toString();
                String date = ((Map<String, String>) ((Map<String, Object>) props.get("날짜")).get("date")).get("start");

                // ✅ 2. DTO 생성 시 ID 전달
                memoList.add(new NotionDTO(id, title, date)); 
            } catch (Exception e) { continue; }
        }
        return memoList;
    }
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + NOTION_TOKEN);
        headers.set("Notion-Version", "2022-06-28");
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}