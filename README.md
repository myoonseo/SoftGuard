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