package com.example.softguard.service;

import com.example.softguard.domain.RiskLevel;
import com.example.softguard.dto.ChartsResponseDto;
import com.example.softguard.dto.HourlyBucketDto;
import com.example.softguard.dto.LabelValueDto;
import com.example.softguard.dto.StatsResponseDto;
import com.example.softguard.projection.AccidentTypeProjection;
import com.example.softguard.projection.LevelCountProjection;
import com.example.softguard.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {
    private final EventRepository eventRepository;
    private final AccidentTypeService accidentTypeService; //추가

    public StatsResponseDto getStats(String location, LocalDate date) {
        Map<RiskLevel, Long> todayCountMap = eventRepository.countTodayGroupByLevel()
                .stream()
                .collect(Collectors.toMap(
                        LevelCountProjection::getLevel,
                        LevelCountProjection::getCount
                ));

        return StatsResponseDto.builder()
                .nearMissToday(buildTodayWarning(todayCountMap))
                .dangerRatio(buildDangerRatio(todayCountMap))
                .nightRatio(buildNightRatio())
                .build();
    }

    // 차트 데이터
    public ChartsResponseDto getCharts(String location, LocalDate date) {
        return ChartsResponseDto.builder()
                .nearMissByHour(buildHourly())
                .incidentTypeRatio(buildTypeRatio())
                .incidentsByWeekday(buildWeekdayFromPublicData())
                .build();
    }

    private List<LabelValueDto> buildWeekdayFromPublicData() {
        int weeks = 52; //

        List<Object[]> raw = List.of(
                new Object[]{"월", 4785L},
                new Object[]{"화", 4969L},
                new Object[]{"수", 5009L},
                new Object[]{"목", 4990L},
                new Object[]{"금", 5390L},
                new Object[]{"토", 4779L},
                new Object[]{"일", 3543L}
        );

        return raw.stream()
                .map(r -> LabelValueDto.builder()
                        .label((String) r[0])
                        .value((double) Math.round((Long) r[1] / (double) weeks))
                        .build())
                .collect(Collectors.toList());
    }

    private Long buildTodayWarning(Map<RiskLevel, Long> countMap) {
        return countMap.getOrDefault(RiskLevel.warning, 0L);
    }

    private Double buildDangerRatio(Map<RiskLevel, Long> countMap) {
        long todayDanger = countMap.getOrDefault(RiskLevel.danger, 0L);
        long todayTotal = countMap.values().stream().mapToLong(Long::longValue).sum();
        return todayTotal == 0 ? 0.0 : Math.round(todayDanger * 1000.0 / todayTotal) / 10.0;
    }

    private Double buildNightRatio() {
        long nighttime = eventRepository.countNighttime();
        long total = eventRepository.count();
        return total == 0 ? 0.0 : Math.round(nighttime * 1000.0 / total) / 10.0;
    }

    private List<HourlyBucketDto> buildHourly() {
        Map<String, Long> hourMap = eventRepository.findHourlyCount().stream()
                .collect(Collectors.toMap(
                        p -> p.getHour(),
                        p -> p.getCount()
                ));

        List<HourlyBucketDto> result = new ArrayList<>();
        for (int i = 0; i <=24; i ++) {
            String hour = String.format("%02d", i);
            result.add(HourlyBucketDto.builder()
                    .bucket(hour)
                    .count(hourMap.getOrDefault(hour, 0L))
                    .build());
        }
        return result;
    }

//    private List<LabelValueDto> buildTypeRatio() {
//        List<AccidentTypeProjection> results = eventRepository.findAccidentTypeCount();
//        long total = results.stream().mapToLong(AccidentTypeProjection::getCount).sum();
//
//        return results.stream()
//                .map(p -> LabelValueDto.builder()
//                        .label(p.getAccidentType())
//                        .value(total == 0 ? 0.0 : Math.round(p.getCount() * 1000.0 / total) / 10.0)
//                        .build())
//                .collect(Collectors.toList());

    private List<LabelValueDto> buildTypeRatio() {
        // AccidentTypeService에서 분류된 데이터 가져오기
        List<Map<String, Object>> classified = accidentTypeService.getAccidentTypeStats();
        long total = classified.stream()
                .mapToLong(m -> (Long) m.get("count"))
                .sum();

        return classified.stream()
                .map(m -> LabelValueDto.builder()
                        .label((String) m.get("accidentType"))
                        .value(total == 0 ? 0.0 : Math.round((Long) m.get("count") * 1000.0 / total) / 10.0)
                        .build())
                .collect(Collectors.toList());
    }
}