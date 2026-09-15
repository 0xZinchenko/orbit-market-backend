package com.zim4ik.spacecatmarket.feature.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class FeatureConfigTest {

    @Autowired
    private Map<String, FeatureFlag> featureFlags;

    @Test
    void bindsFeatureFlagsFromApplicationProperties() {
        assertThat(featureFlags.get("cosmoCats").enabled()).isTrue();
        assertThat(featureFlags.get("kittyProducts").enabled()).isFalse();
    }
}
