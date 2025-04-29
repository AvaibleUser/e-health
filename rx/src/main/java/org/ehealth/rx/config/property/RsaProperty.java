package org.ehealth.rx.config.property;

import java.security.interfaces.RSAPublicKey;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("security.rsa")
public record RsaProperty(
        RSAPublicKey publicKey) {
}
