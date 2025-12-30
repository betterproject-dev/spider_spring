package com.example.spider_spring.domain;


import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SensorsDTO {
	private Integer id;
	private Integer machineNumber;
	@JsonFormat(pattern= "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private Timestamp createdAt;
	private Float temperature_DS18B20;
	private Float humidity;
	private Float noise;
	private Boolean leak;
	
	public SensorsDTO(Sensors sensors) {
		this.id = sensors.getId();
		this.machineNumber = sensors.getMachine().getId();
		this.createdAt = sensors.getCreatedAt();
		this.temperature_DS18B20 = sensors.getTemperature_DS18B20();
		this.humidity = sensors.getHumidity();
		this.noise = sensors.getNoise();
		this.leak = sensors.getLeak();
		
	}

}
