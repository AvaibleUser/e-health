package org.ehealth.hr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import feign.RequestInterceptor;
import feign.RequestTemplate;

@Configuration
public class ClientConfig {

    @Bean
    RequestInterceptor authorizationRequestInterceptor() {
        return new AuthorizationRequestInterceptor();
    }

    public static class AuthorizationRequestInterceptor implements RequestInterceptor {

        @Override
        public void apply(RequestTemplate template) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null) {
                return;
            }
            Jwt jwt = (Jwt) authentication.getPrincipal();
            String token = jwt.getTokenValue();
            template.header("Authorization", "Bearer " + token);
        }
    }
}
