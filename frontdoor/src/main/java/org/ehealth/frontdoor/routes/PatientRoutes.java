package org.ehealth.frontdoor.routes;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.stripPrefix;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

import org.ehealth.frontdoor.config.property.PatientServiceProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
public class PatientRoutes {

    @Bean
    RouterFunction<ServerResponse> routePatientService(PatientServiceProperty servicesProperty) {
        return route()
                .before(stripPrefix(1))
                .route(path("/ward/v1/**"), http(servicesProperty.url()))
                .build();
    }
}
