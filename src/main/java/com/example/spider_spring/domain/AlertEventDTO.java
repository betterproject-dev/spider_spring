package com.example.spider_spring.domain;

import java.sql.Timestamp;

import lombok.Data;


@Data
public class AlertEventDTO {
	
	private Integer id;
    private Integer machineId;
    private String machineLocation;
    private AlertLevel level;
    private Float dangerScore;
    private String title;
    private String message;
    private String snapshot;
    private Timestamp startedAt;
    private Timestamp endedAt;
    private Timestamp acknowledgedAt;
    private String activeKey;
    
    private String mode;
    
    public AlertEventDTO(AlertEvent alertEvent) {
    	this.id = alertEvent.getId();
        this.machineId = (alertEvent.getMachine() != null ? alertEvent.getMachine().getId() : null);
        this.machineLocation = (alertEvent.getMachine() != null ? alertEvent.getMachine().getLocation() : null);
        this.level = alertEvent.getLevel();
        this.dangerScore = alertEvent.getDangerScore();
        this.title = alertEvent.getTitle();
        this.message = alertEvent.getMessage();
        this.snapshot = alertEvent.getSnapshot();
        this.startedAt = alertEvent.getStartedAt();
        this.endedAt = alertEvent.getEndedAt();
        this.acknowledgedAt = alertEvent.getAcknowledgedAt();
        this.activeKey = alertEvent.getActiveKey();
    }
}
