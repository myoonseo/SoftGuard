package com.example.softguard.service;

import com.example.softguard.projection.RawAccidentProjection;
import com.example.softguard.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccidentTypeService {

    private final EventRepository eventRepository;

    private static final List<String> PEDESTRIAN_KEYWORDS =
            List.of("보행자", "사람", "행인");
    private static final List<String> PM_KEYWORDS =
            List.of("킥보드", "전동킥보드", "pm", "자전거");
    private static final List<String> VEHICLE_KEYWORDS =
            List.of("승용차", "suv", "자차", "자율주행차", "택시", "버스", "트럭");

    private String normalizeObject(String raw) {
        if (raw == null) return "기타";
        String v = raw.toLowerCase();
        if (PEDESTRIAN_KEYWORDS.stream().anyMatch(v::contains)) return "보행자";
        if (PM_KEYWORDS.stream().anyMatch(v::contains))         return "PM";
        if (VEHICLE_KEYWORDS.stream().anyMatch(v::contains))    return "차량";
        return "기타";
    }

    private String classifyType(String obj1, String obj2) {
        String cat1 = normalizeObject(obj1);
        String cat2 = normalizeObject(obj2);

        Set<String> pair = new HashSet<>(Arrays.asList(cat1, cat2));
        if (pair.containsAll(Arrays.asList("보행자", "차량"))) return "보행자-차량";
        if (cat1.equals("차량") && cat2.equals("차량"))         return "차량-차량";
        if (pair.containsAll(Arrays.asList("차량", "PM")))      return "차량-PM";
        return "기타";
    }

    public List<Map<String, Object>> getAccidentTypeStats() {
        Map<String, Long> grouped = eventRepository.findRawAccidentTypeCount()
                .stream()
                .collect(Collectors.groupingBy(
                        row -> classifyType(row.getObject1(), row.getObject2()),
                        Collectors.summingLong(RawAccidentProjection::getCount)
                ));

        // 프론트엔드에서 accidentType, count 키로 받을 수 있게 변환
        return grouped.entrySet().stream()
                .map(e -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("accidentType", e.getKey());
                    map.put("count", e.getValue());
                    return map;
                })
                .collect(Collectors.toList());
    }
}