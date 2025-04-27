package org.ehealth.gatekeeper.service.util;

import org.ehealth.gatekeeper.domain.dto.TokenDto;
import org.ehealth.gatekeeper.domain.dto.UserDto;

public interface ITokenService {

    TokenDto generateToken(UserDto user);
}
