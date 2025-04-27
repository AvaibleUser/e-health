package org.ehealth.frontdoor.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("route.services.auth")
public record AuthServiceProperty(
        String url) {
}
