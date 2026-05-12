package com.example.softguard.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "insight_summary")
@Getter @Setter
@NoArgsConstructor
public class InsightSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 요약 시간 범위 (예: "10:00~11:00")
    @Column(name = "time_range", nullable = false)
    private String timeRange;

    // 요약 시작 시각
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    // 요약 종료 시각
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    // HCX-007이 생성한 통합 인사이트 요약
    @Column(columnDefinition = "TEXT", nullable = false)
    private String summary;

    // HCX-007이 생성한 운영 제안
    @Column(columnDefinition = "TEXT")
    private String suggestion;

    // 사고 전환 확률 (3단계)
    @Column(name = "conversion_probability")
    private Double conversionProbability;

    // 상위 퍼센타일 (3단계)
    private Integer percentile;

    // 해당 시간대 이벤트 총 건수
    @Column(name = "event_count")
    private Integer eventCount;

    // 레코드 생성 시각
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
