package com.example.spider_spring.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "heartbeat")
public class Heartbeat {

	@Id
    @Column(name = "machine_id")
    private Integer machineId;

    @Column(name = "last_seen")
    private LocalDateTime lastSeen;

    public void updateNow() {
        this.lastSeen = LocalDateTime.now();
    }
}
