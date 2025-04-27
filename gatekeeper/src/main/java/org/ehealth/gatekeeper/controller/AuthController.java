package org.ehealth.gatekeeper.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.security.authentication.UsernamePasswordAuthenticationToken.unauthenticated;

import java.util.Map;
import java.util.Optional;

import org.ehealth.gatekeeper.domain.dto.AddUserDto;
import org.ehealth.gatekeeper.domain.dto.AuthUserDto;
import org.ehealth.gatekeeper.domain.dto.ConfirmUserDto;
import org.ehealth.gatekeeper.domain.dto.TokenDto;
import org.ehealth.gatekeeper.domain.exception.FailedAuthenticateException;
import org.ehealth.gatekeeper.domain.exception.RequestConflictException;
import org.ehealth.gatekeeper.service.ICodesService;
import org.ehealth.gatekeeper.service.IUserService;
import org.ehealth.gatekeeper.service.util.IEmailService;
import org.ehealth.gatekeeper.service.util.ITemplateService;
import org.ehealth.gatekeeper.service.util.ITokenService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IUserService userService;
    private final ITokenService tokenService;
    private final ICodesService codesService;
    private final IEmailService emailService;
    private final ITemplateService templateRendererService;
    private final AuthenticationManager authManager;

    @PostMapping("/sign-up")
    @ResponseStatus(CREATED)
    public void signUp(@RequestBody @Valid AddUserDto user) {
        userService.registerUser(user, true);

        String code = codesService.generateConfirmCode(user.email());
        Map<String, Object> templateVariables = Map.of("code", code.toCharArray(), "user", user);
        String confirmationHtml = templateRendererService.renderTemplate("sign-up-confirmation", templateVariables);

        try {
            emailService.sendHtmlEmail("E-Health", user.email(),
                    "Confirmacion de usuario en E-Health", confirmationHtml);
        } catch (MessagingException e) {
            throw new RequestConflictException("No se pudo enviar el correo de confirmacion");
        }
        userService.registerUser(user, false);
    }

    @PutMapping("/sign-up")
    public TokenDto confirmSignUp(@RequestBody @Valid ConfirmUserDto user) {
        boolean confirmed = codesService.confirmCode(user.email(),
                user.code());
        if (!confirmed) {
            throw new FailedAuthenticateException("No se pudo confirmar la cuenta");
        }

        return userService.findUserByEmail(user.email())
                .map(tokenService::generateToken)
                .orElseThrow(() -> new InsufficientAuthenticationException("No se encontro el registro del usuario"));
    }

    @PostMapping("/sign-in")
    public Optional<TokenDto> signIn(@RequestBody @Valid AuthUserDto user) {
        var authenticableUser = unauthenticated(user.email(), user.password());
        authManager.authenticate(authenticableUser);

        return userService.findUserByEmail(user.email())
                .map(tokenService::generateToken);
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello World";
    }
}
