package org.ehealth.gatekeeper.service.util;

import java.time.Instant;
import java.util.List;

import org.ehealth.gatekeeper.config.property.TokenProperty;
import org.ehealth.gatekeeper.domain.dto.TokenDto;
import org.ehealth.gatekeeper.domain.dto.UserDto;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService implements ITokenService {

    private final JwtEncoder jwtEncoder;
    private final TokenProperty tokenProperty;

    public TokenDto generateToken(UserDto user) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("gatekeeper")
                .issuedAt(now)
                .expiresAt(now.plus(tokenProperty.expirationTime(), tokenProperty.timeUnit()))
                .subject(String.valueOf(user.id()))
                .claim("auths", List.of("ROLE_".concat(user.roleName())))
                .claim("cui", user.cui())
                .build();

        String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        return new TokenDto(token, user);
    }
}
