package com.zim4ik.spacecatmarket.feature.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class FeatureConfig {

    @Bean
    @ConfigurationProperties(prefix = "feature")
    public Map<String, FeatureFlag> featureFlags() {
        return new HashMap<>();
    }
}
