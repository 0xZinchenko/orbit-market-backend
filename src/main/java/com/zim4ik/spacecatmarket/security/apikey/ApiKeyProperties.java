package com.zim4ik.spacecatmarket.security.apikey;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

@ConfigurationProperties(prefix = "security.api-key")
public record ApiKeyProperties(Set<String> validKeys) {
}
