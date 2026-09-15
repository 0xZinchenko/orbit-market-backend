package com.zim4ik.spacecatmarket.feature.exception;

public class FeatureNotAvailableException extends RuntimeException {

    public FeatureNotAvailableException(String featureName) {
        super("Feature '" + featureName + "' is not available");
    }
}
