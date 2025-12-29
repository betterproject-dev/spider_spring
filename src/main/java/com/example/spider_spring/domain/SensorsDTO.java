package com.example.spider_spring.domain;


import java.sql.Timestamp;

import lombok.Data;

@Data
public class SensorsDTO {
	private Integer id;
	private Integer machineNumber;
	private Timestamp createdAt;
	private Float temperature;
	private Float humidity;
	private Float noise;
	private Boolean leak;
	
	public SensorsDTO(Sensors sensors) {
		this.id = sensors.getId();
		this.machineNumber = sensors.getMachine().getId();
		this.createdAt = sensors.getCreatedAt();
		this.temperature = sensors.getTemperature();
		this.humidity = sensors.getHumidity();
		this.noise = sensors.getNoise();
		this.leak = sensors.getLeak();
		
	}

}
