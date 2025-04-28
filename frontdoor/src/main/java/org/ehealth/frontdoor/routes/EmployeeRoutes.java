package org.ehealth.frontdoor.routes;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.stripPrefix;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

import org.ehealth.frontdoor.config.property.EmployeeServiceProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
public class EmployeeRoutes {

    @Bean
    RouterFunction<ServerResponse> routeAuthenticationService(EmployeeServiceProperty servicesProperty) {
        return route()
                .before(stripPrefix(1))
                .route(path("/v1/hr/**"), http(servicesProperty.url()))
                .build();
    }
}
