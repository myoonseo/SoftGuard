package com.example.softguard.projection;

import com.example.softguard.domain.RiskLevel;

public interface LevelCountProjection {
    RiskLevel getLevel();
    Long getCount();
}
