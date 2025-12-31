package com.example.spider_spring.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RejectionRatesDTO {
    private String createdAt;  // 그래프의 X축 (날짜: "12-29")
    private Double rejectionRate;  // 그래프의 Y축 (불량률)
}