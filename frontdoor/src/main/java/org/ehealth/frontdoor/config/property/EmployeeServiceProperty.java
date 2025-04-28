package org.ehealth.frontdoor.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("route.services.employee")
public record EmployeeServiceProperty(
        String url) {
}
