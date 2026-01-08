package com.example.spider_spring.service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.spider_spring.domain.Sensors;
import com.example.spider_spring.domain.SensorsDTO;
import com.example.spider_spring.repository.SensorsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SensorService {

	private final SensorsRepository sensorsRepository;
	
	// [일간 추이] 선택한 날짜의 0~23시 평균 (24개 결과)
	
	public List<SensorsDTO> getSensorDataByDay(String date, Integer machineNumber) {
		
	try {	
		
		ZoneId korea = ZoneId.of("Asia/Seoul");
		// 1. 문자열을 날짜 객체로 변환 (공백 에러 방지)
		LocalDate localDate = LocalDate.parse(date.trim());
		
		// 한국시간으로 변환 	
		LocalDateTime startDay = localDate.atStartOfDay();
		LocalDateTime endDay = localDate.atTime(23, 59, 59);
		
		Timestamp start = Timestamp.valueOf(startDay);
		Timestamp end = Timestamp.valueOf(endDay);
		
        System.out.println("=== 검색 조건 ===");
        System.out.println("날짜: " + date);
        System.out.println("기계번호: " + machineNumber);
        System.out.println("시작: " + start);
        System.out.println("끝: " + end);

	
		// 한 번에 하루치 데이터 다 가져와서 평균 계싼  
        List<Sensors> allSensorsData = sensorsRepository
                .findByMachineNumberAndDate(machineNumber, date);
	
	// 빈 리스트 생성해서 담기 
	List<SensorsDTO> result = new ArrayList<>();
	
	// 0~23까지 반복하면서 평균 계산
	for (int h = 0; h < 24; h++) {
		int hour = h;
		
		List<Sensors> hourData = allSensorsData.stream()
			.filter(s -> {
				LocalDateTime koreanTime = s.getCreatedAt()
						.toInstant()
						.atZone(korea)
						.toLocalDateTime();
				return koreanTime.getHour() == hour;
				})
				.toList();
	
        if (hourData.size() > 0) {
            System.out.println(hour + "시: " + hourData.size() + "개 데이터");
        }
		
		// 라벨 생성 예 : 2025-12-29 01:00:00
		String label = date + " " + String.format("%02d", h) + ":00:00";
		result.add(calculateAverage(hourData, label, machineNumber));
 	} 
	return result;
	
	} catch (Exception e) {
		System.out.println("일간 데이터 처리 중 에러 : " + e.getMessage());
		  e.printStackTrace(); // 📌 전체 에러 스택 출력
		throw e;
	}
}

	
	// 7일간 변동 추이 (최근 7일간 일별 평균 - 7개 결과 )
	
	public List<SensorsDTO> getSensorDataByWeek(Integer machineNumber) {
		
		try {
			ZoneId korea = ZoneId.of("Asia/Seoul");
			List<SensorsDTO> result = new ArrayList<>();
			
			// 오늘 ~ 6일 전까지 
			LocalDate today = LocalDate.now();
			
			for (int i=6; i>= 0; i--) {
				LocalDate targetDate = today.minusDays(i);
				String dateStr = targetDate.toString();   // "2025-12-29"
				
				System.out.println("===" + dateStr + "처리 중 ===");
				
	            // 해당 날짜의 하루치 데이터 가져오기
	            List<Sensors> dayData = sensorsRepository
	                .findByMachineNumberAndDate(machineNumber, dateStr);
	            
	            System.out.println("데이터 개수: " + dayData.size());
	            
	            // 하루 전체 평균 (시간 구분 없이)
	            String label = dateStr + " 00:00:00";
	            result.add(calculateAverage(dayData, label, machineNumber));
	        }
	        
	        return result;
	        
	    } catch (Exception e) {
	        System.out.println("주간 데이터 처리 중 에러: " + e.getMessage());
	        e.printStackTrace();
	        throw e;
	    }
	}
		
	
	/*
	 * 	// [공통 로직] 평균값 계산 바구니 채우기 ( 해당 클래스에서 공통으로 사용될 것)
	 * */
	
	private SensorsDTO calculateAverage(List<Sensors> data, String label, Integer MachineNumber) {
		
		SensorsDTO dto = new SensorsDTO();
		LocalDateTime ldt = null;
//		LocalDateTime ldt = LocalDateTime.parse(label.replace(" ", "T"));
//		dto.setCreatedAt(Timestamp.valueOf(ldt));
		
	    try {
	        System.out.println("파싱 시도: " + label);
	        ldt = LocalDateTime.parse(label.replace(" ", "T"));
	        System.out.println("파싱 성공: " + ldt);
	        dto.setCreatedAt(Timestamp.valueOf(ldt));
	    } catch (Exception e) {
	        System.out.println("파싱 에러: " + e.getClass().getName());
	        System.out.println("메시지: " + e.getMessage());
	        e.printStackTrace();
	        throw e;
	    }
	    
	    // 📌 가상 ID 생성 (날짜시간을 숫자로 변환)
	    // 예: "2025-12-29 16:00:00" → 2025122916 (년월일시)

	    int virtualId = ldt.getYear() * 10000 + 
	                     ldt.getMonthValue() * 100 + 
	                     ldt.getDayOfMonth(); 
	                     
	    
	    dto.setId(virtualId);
		dto.setMachineNumber(MachineNumber);
		
		if ( data != null && !data.isEmpty()) {
			// 온도 평균: 리스트를 돌며 점수를 다 더한 뒤 개수로 나눔
			float tempAvg = (float) data.stream()
					.filter(s -> s.getTemperature_DS18B20() != null) // null 제외
					.mapToDouble(Sensors::getTemperature_DS18B20 ) // 숫자들만 뽑아내서 
					.average()
					.orElse(0.0);
			dto.setTemperature_DS18B20(Math.round(tempAvg * 100.0) / 100.0f);
			
			// 습도 평균
			float humAvg = (float) data.stream()
					.filter(s -> s.getHumidity() != null) // null 제외
					.mapToDouble(Sensors::getHumidity)
					.average()
					.orElse(0.0);
			dto.setHumidity(Math.round(humAvg * 100.0) / 100.0f);
			
			// 소음 평균
			float noiseAvg = (float) data.stream()
					.filter(s -> s.getNoise() != null) // null 제외
					.mapToDouble(Sensors::getNoise)
					.average()
					.orElse(0.0);
			dto.setNoise(Math.round(noiseAvg * 100.0) / 100.0f);
			
			// 누수 여부: 하나라도 true가 있으면 true
	        dto.setLeak(data.stream()
	        		.filter(s -> s.getLeak() != null)
	        		.anyMatch(Sensors::getLeak));
		} else {
			// 데이터가 없는 시간대 모두 0으로 초기화
			dto.setTemperature_DS18B20(0.0f);
			dto.setHumidity(0.0f);
			dto.setNoise(0.0f);
			dto.setLeak(false);	
		}
		return dto;
	}
}
