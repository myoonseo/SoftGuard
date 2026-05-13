package com.example.softguard.repository;

import com.example.softguard.domain.Event;
import com.example.softguard.domain.RiskLevel;
import com.example.softguard.projection.AccidentTypeProjection;
import com.example.softguard.projection.HourlyProjection;
import com.example.softguard.projection.LevelCountProjection;
import com.example.softguard.projection.RawAccidentProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByCreatedAtAfterOrderByCreatedAtAsc(LocalDateTime createdAtAfter);

        // 시간대별 Near-miss 발생건수
        @Query("SELECT SUBSTRING(e.time, 1, 2) as hour, COUNT(e) as count " +
                "FROM Event e " +
                "WHERE e.level = 'warning' " +
                "GROUP BY SUBSTRING(e.time, 1, 2) " +
                "ORDER BY hour")
        List<HourlyProjection> findHourlyCount();

        // 사고 유형 비율
        @Query("SELECT CONCAT(e.object1, '-', e.object2) as accidentType, COUNT(e) as count " +
                "FROM Event e " +
                "WHERE e.object1 IS NOT NULL AND e.object2 IS NOT NULL " +
                "GROUP BY e.object1, e.object2")
        List<AccidentTypeProjection> findAccidentTypeCount();

        //추가
        // EventRepository.java
        @Query("SELECT e.object1 as object1, e.object2 as object2, COUNT(e) as count " +
                "FROM Event e " +
                "WHERE e.object1 IS NOT NULL AND e.object2 IS NOT NULL " +
                "GROUP BY e.object1, e.object2")
        List<RawAccidentProjection> findRawAccidentTypeCount();

        //요일별 발생 건수-> 하드코딩 진행

        // Near-miss 누적(오늘)
        //@Query("SELECT COUNT(e) FROM Event e " +
        //        "WHERE e.level = :level " +
        //        "AND DATE(e.createdAt) = CURRENT_DATE")
        //Long countTodayByLevel(@Param("level") RiskLevel level);

        //@Query("SELECT e.level as level, COUNT(e) as count " +
        //    "FROM Event e " +
        //    "WHERE DATE(e.createdAt) = CURRENT_DATE " +
        //    "GROUP BY e.level")
        //List<LevelCountProjection> countTodayGroupByLevel();
        @Query("SELECT COUNT(e) FROM Event e " +
                "WHERE e.level = :level " +
                "AND DATE(FUNCTION('CONVERT_TZ', e.createdAt, '+00:00', '+09:00')) = DATE(FUNCTION('CONVERT_TZ', CURRENT_TIMESTAMP, '+00:00', '+09:00'))")
        Long countTodayByLevel(@Param("level") RiskLevel level);

        @Query("SELECT e.level as level, COUNT(e) as count " +
            "FROM Event e " +
            "WHERE DATE(FUNCTION('CONVERT_TZ', e.createdAt, '+00:00', '+09:00')) = DATE(FUNCTION('CONVERT_TZ', CURRENT_TIMESTAMP, '+00:00', '+09:00')) " +
            "GROUP BY e.level")
        List<LevelCountProjection> countTodayGroupByLevel();

        //실제 사고 전확률

        // 야간(18~22시) 비율 -> 테스트 때문에 10시 12시로 변경
        @Query(value = "SELECT COUNT(*) FROM event " +
                "WHERE CONVERT(SUBSTRING(time, 1, 2), UNSIGNED) BETWEEN 10 AND 12",
                nativeQuery = true)
        Long countNighttime();

        //summary 관련
        @Query("SELECT e FROM Event e WHERE e.createdAt BETWEEN :start AND :end ORDER BY e.createdAt ASC")
        List<Event> findEventsBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

        //summary가 null인 Event 조회
        @Query("SELECT e FROM Event e WHERE e.videoUrl IS NOT NULL AND e.summary IS NULL ORDER BY e.createdAt ASC")
        List<Event> findPendingEvents();

    // summary가 존재하는 Event 조회 (이벤트 스트림 카드용)
        List<Event> findBySummaryIsNotNullOrderByCreatedAtDesc();

        List<Event> findTop4ByProcessedFalseOrderByCreatedAtAsc();
}


