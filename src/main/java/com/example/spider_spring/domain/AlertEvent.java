package com.example.spider_spring.domain;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "alert_events",
	   indexes = {
			   @Index(name = "idx_alert_machine", columnList = "machine_number"),
	           @Index(name = "idx_alert_level", columnList = "level"),
	           @Index(name = "idx_alert_started", columnList = "started_at"),
	           @Index(name = "idx_alert_ended", columnList = "ended_at"),
	           @Index(name = "idx_alert_ack", columnList = "acknowledged_at")
	   })
public class AlertEvent {
	

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Flask: machine_number FK -> machines.id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "machine_number")
    private Machines machine;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AlertLevel level = AlertLevel.WARNING;

    @Column(name = "danger_score")
    private Float dangerScore;

    @Column(nullable = false, length = 100)
    private String title = "긴급 알림";

    @Column(nullable = false, length = 255)
    private String message;

    // MySQL JSON: 가장 안전 = String으로 저장/조회 (필요하면 나중에 Object로 변환)
    @Column(columnDefinition = "json")
    private String snapshot;

    @Column(name = "started_at", nullable = false)
    private Timestamp startedAt;

    @Column(name = "ended_at")
    private Timestamp endedAt;

    @Column(name = "acknowledged_at")
    private Timestamp acknowledgedAt;

    // started_at 기본값: Flask가 넣든, Spring이 넣든 둘 중 하나는 항상 넣어야 함
    @PrePersist
    protected void onCreate() {
        if (startedAt == null) {
            startedAt = new Timestamp(System.currentTimeMillis());
        }
        if (level == null) {
            level = AlertLevel.WARNING;
        }
        if (title == null) {
            title = "긴급 알림";
        }
    }

}
