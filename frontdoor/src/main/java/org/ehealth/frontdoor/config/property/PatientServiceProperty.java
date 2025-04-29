package org.ehealth.frontdoor.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("route.services.patient")
public record PatientServiceProperty(
        String url) {
}
