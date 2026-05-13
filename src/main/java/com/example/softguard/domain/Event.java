package com.example.softguard.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="event")
@Getter @Setter
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private String time;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)

    private RiskLevel level;
    @Column(nullable = false)
    private String location;

    private String object1;
    private String object2;
    private String state;

    @Column(name = "vehicle_count")
    private Integer vehicleCount;

    @Column(name = "pedestrian_count")
    private Integer pedestrianCount;

    // ✅ 추가 - 감지된 PM(전동킥보드) 수
    @Column(name = "pm_count")
    private Integer pmCount;

    // ✅ 추가 - 예상 충돌 시간 (예: "2.3sec")
    @Column(name = "collision_time")
    private String collisionTime;


    private String action;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    //추가 - 영상 URL 부분
    @Column(name = "video_url")
    private String videoUrl;

    // 영상 썸네일 관련 부분
    @Column(name = "thumbnail_url")
    private String thumbnailUrl;


    //summary 부분 추가!
    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(name = "is_processed", nullable = false)
    private boolean processed = false;

}
