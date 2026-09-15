package com.zim4ik.spacecatmarket.feature.aspect;

import com.zim4ik.spacecatmarket.feature.exception.FeatureNotAvailableException;
import com.zim4ik.spacecatmarket.feature.service.FeatureToggleService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class FeatureToggleAspect {

    private final FeatureToggleService featureToggleService;

    public FeatureToggleAspect(FeatureToggleService featureToggleService) {
        this.featureToggleService = featureToggleService;
    }

    @Around("@annotation(requiresFeature)")
    public Object checkFeatureToggle(ProceedingJoinPoint joinPoint, RequiresFeature requiresFeature) throws Throwable {
        String featureName = requiresFeature.value();

        if (!featureToggleService.isEnabled(featureName)) {
            throw new FeatureNotAvailableException(featureName);
        }

        return joinPoint.proceed();
    }
}
