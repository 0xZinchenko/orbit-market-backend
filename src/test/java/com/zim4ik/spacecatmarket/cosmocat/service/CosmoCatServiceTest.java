package com.zim4ik.spacecatmarket.cosmocat.service;

import com.zim4ik.spacecatmarket.feature.aspect.FeatureToggleAspect;
import com.zim4ik.spacecatmarket.feature.config.FeatureFlag;
import com.zim4ik.spacecatmarket.feature.exception.FeatureNotAvailableException;
import com.zim4ik.spacecatmarket.feature.service.FeatureToggleService;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CosmoCatServiceTest {

    @Test
    void getCosmoCats_whenFeatureEnabled_returnsCats() {
        CosmoCatService proxy = createProxy(true);

        List<String> result = proxy.getCosmoCats();

        assertThat(result).containsExactly("Nebula", "Comet", "Orion");
    }

    @Test
    void getCosmoCats_whenFeatureDisabled_throwsFeatureNotAvailableException() {
        CosmoCatService proxy = createProxy(false);

        assertThatThrownBy(proxy::getCosmoCats)
                .isInstanceOf(FeatureNotAvailableException.class)
                .hasMessage("Feature 'cosmoCats' is not available");
    }

    private CosmoCatService createProxy(boolean enabled) {
        FeatureToggleService featureToggleService =
                new FeatureToggleService(Map.of("cosmoCats", new FeatureFlag(enabled)));

        AspectJProxyFactory factory = new AspectJProxyFactory(new CosmoCatService());
        factory.addAspect(new FeatureToggleAspect(featureToggleService));

        return factory.getProxy();
    }
}
