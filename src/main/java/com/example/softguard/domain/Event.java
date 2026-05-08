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

    @Column(nullable = false)
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
    private Integer vehicle_count;

    @Column(name = "pedestrian_count")
    private Integer pedestrian_count;

    private String action;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    //추가 - 영상 URL 부분
    @Column(name = "video_url")
    private String videoUrl;


}
