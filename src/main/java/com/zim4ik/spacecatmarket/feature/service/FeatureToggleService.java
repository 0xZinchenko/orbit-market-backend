package com.zim4ik.spacecatmarket.feature.service;

import com.zim4ik.spacecatmarket.feature.config.FeatureFlag;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FeatureToggleService {

    private final Map<String, FeatureFlag> featureFlags;

    public FeatureToggleService(Map<String, FeatureFlag> featureFlags) {
        this.featureFlags = featureFlags;
    }

    public boolean isEnabled(String featureName) {
        FeatureFlag flag = featureFlags.get(featureName);
        return flag != null && flag.enabled();
    }
}
