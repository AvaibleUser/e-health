package org.ehealth.ward.util;

import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

public class JwtBuilder {

    public static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwt(long userId, String... roles) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(b -> b.subject(String.valueOf(userId)).claim("auths", roles));
    }
}