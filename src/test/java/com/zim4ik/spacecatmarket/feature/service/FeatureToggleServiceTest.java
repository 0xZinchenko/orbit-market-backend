package com.zim4ik.spacecatmarket.feature.service;

import com.zim4ik.spacecatmarket.feature.config.FeatureFlag;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class FeatureToggleServiceTest {

    @Test
    void isEnabled_whenFlagIsTrue_returnsTrue() {
        FeatureToggleService service = new FeatureToggleService(Map.of("cosmoCats", new FeatureFlag(true)));

        assertThat(service.isEnabled("cosmoCats")).isTrue();
    }

    @Test
    void isEnabled_whenFlagIsFalse_returnsFalse() {
        FeatureToggleService service = new FeatureToggleService(Map.of("kittyProducts", new FeatureFlag(false)));

        assertThat(service.isEnabled("kittyProducts")).isFalse();
    }

    @Test
    void isEnabled_whenFlagIsMissing_returnsFalse() {
        FeatureToggleService service = new FeatureToggleService(Map.of());

        assertThat(service.isEnabled("unknownFeature")).isFalse();
    }
}
