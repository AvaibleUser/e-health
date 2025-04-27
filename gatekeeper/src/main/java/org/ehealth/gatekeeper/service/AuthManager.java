package org.ehealth.gatekeeper.service;

import static org.springframework.security.authentication.UsernamePasswordAuthenticationToken.authenticated;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

import org.ehealth.gatekeeper.domain.entity.UserEntity;
import org.ehealth.gatekeeper.domain.exception.BadRequestException;
import org.ehealth.gatekeeper.domain.exception.FailedAuthenticateException;
import org.ehealth.gatekeeper.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthManager implements AuthenticationManager {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final ConcurrentMap<String, String> signUpConfirmationCodes;

    @Override
    @Transactional
    public Authentication authenticate(Authentication authUser) throws AuthenticationException {
        String email = authUser.getPrincipal().toString();
        String password = authUser.getCredentials().toString();

        if (signUpConfirmationCodes.containsKey(email)) {
            throw new FailedAuthenticateException("La cuenta aun no se ha confirmado");
        }

        // UserEntity user = userRepository.findByEmail(email, UserEntity.class)
        userRepository.findByEmail(email, UserEntity.class)
                .filter(dbUser -> encoder.matches(password, dbUser.getPassword()))
                .orElseThrow(() -> new BadRequestException("El email o la contraseña es incorrecta"));

        // TODO: get authorities
        return authenticated(email, password, List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }
}
