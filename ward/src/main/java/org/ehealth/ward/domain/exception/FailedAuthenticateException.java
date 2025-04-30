package org.ehealth.ward.domain.exception;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = UNAUTHORIZED)
public class FailedAuthenticateException extends RuntimeException {

    public FailedAuthenticateException(String message) {
        super(message);
    }
}
