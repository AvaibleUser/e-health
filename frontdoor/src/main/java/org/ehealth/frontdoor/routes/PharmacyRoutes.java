package org.ehealth.frontdoor.routes;

import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

import org.ehealth.frontdoor.config.property.PharmacyServiceProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
public class PharmacyRoutes {

    @Bean
    RouterFunction<ServerResponse> routePharmacyService(PharmacyServiceProperty servicesProperty) {
        return route()
                .route(path("/rx/**"), http(servicesProperty.url()))
                .build();
    }
}