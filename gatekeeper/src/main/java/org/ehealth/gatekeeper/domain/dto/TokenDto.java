package org.ehealth.gatekeeper.domain.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

public record TokenDto(
        String token,
        @JsonUnwrapped UserDto user) {
}
