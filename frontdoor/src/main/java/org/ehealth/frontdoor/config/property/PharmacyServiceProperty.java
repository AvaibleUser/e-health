package org.ehealth.frontdoor.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("route.services.pharmacy")
public record PharmacyServiceProperty(
        String url) {
}
