package com.zim4ik.spacecatmarket.feature.aspect;

import com.zim4ik.spacecatmarket.feature.exception.FeatureNotAvailableException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class FeatureToggleAspectTest {

    @Autowired
    private ProbeService probeService;

    @Test
    void whenFeatureEnabled_methodExecutesNormally() {
        assertThat(probeService.enabledFeature()).isEqualTo("ok");
    }

    @Test
    void whenFeatureDisabled_throwsFeatureNotAvailableException() {
        assertThatThrownBy(() -> probeService.disabledFeature())
                .isInstanceOf(FeatureNotAvailableException.class)
                .hasMessage("Feature 'kittyProducts' is not available");
    }

    @TestConfiguration
    static class ProbeConfig {
        @Bean
        ProbeService probeService() {
            return new ProbeService();
        }
    }

    static class ProbeService {

        @RequiresFeature("cosmoCats")
        String enabledFeature() {
            return "ok";
        }

        @RequiresFeature("kittyProducts")
        String disabledFeature() {
            return "ok";
        }
    }
}
