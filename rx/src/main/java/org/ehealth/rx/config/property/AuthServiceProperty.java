package org.ehealth.rx.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("route.services.auth")
public record AuthServiceProperty(
        String url) {
}
