## Base URL
https://softguard-ah9q.onrender.com


### 1. 이벤트 목록 조회
GET /api/events/stream

### 2. SSE 실시간 구독
GET /api/events/stream/subscribe

## 3. 통계 카드 조회
GET /api/analytics/stats

```json
{
"nearMissToday": 20,
"dangerRatio": 15.6,
"nightRatio": 20.4
}
```

## 4. 차트 데이터 조회
GET /api/analytics/charts

```json
{
"nearMissByHour": [
{ "bucket": "00", "count": 0 },
{ "bucket": "02", "count": 0 },
{ "bucket": "08", "count": 1 },
{ "bucket": "16", "count": 6 }
],
"incidentTypeRatio": [
{ "label": "차량-보행자", "value": 41.7 },
{ "label": "차량-차량", "value": 38.9 }
],
"incidentsByWeekday": [
    { "label": "월", "value": 92
    },
    { "label": "화","value": 95.6
    },
    { "label": "수",  "value": 96.3
    },
    { "label": "목",  "value": 96
    },
    { "label": "금",  "value": 103.7
    },
    { "label": "토","value": 91.9
    },
    { "label": "일","value": 68.1
    }
  ]
}
```

#LLM 사건 요약 및 인사이트 
GET /api/insights/latest
```json
{
  "conversionProbability": null,
  "percentile": null,
  "suggestion": "신호 교차로 구간에 실시간 알림 시스템 설치를 확대하고, 교차로 진입로에 가변형 속도 제한 표지판 도입을 검토해야 합니다. 또한 보행자 밀집 구간의 PM 이용자 속도 제한을 위한 바닥 LED 표시등 설치를 권장합니다.",
  "summary": "최근 1시간 동안 총 6건의 차량 위험 상황이 발생했으며, 그중 3건이 Near-miss 사례로 기록되었습니다. 가장 빈번한 위험 위치는 신호 교차로였으며, 주요 충돌 객체 조합으로는 차량과 보행자의 근접 상황이 2건으로 나타났습니다. 위험 행동으로는 교차로 내 신호 위반 및 정지선 초과 정차가 다수 관찰되었습니다.",
  "timeRange": "09:23~10:23"
}
```
