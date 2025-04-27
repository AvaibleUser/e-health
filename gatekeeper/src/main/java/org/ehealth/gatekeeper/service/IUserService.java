package org.ehealth.gatekeeper.service;

import java.util.Optional;

import org.ehealth.gatekeeper.domain.dto.AddUserDto;
import org.ehealth.gatekeeper.domain.dto.UserDto;

public interface IUserService {

    Optional<UserDto> findUserByEmail(String email);

    void registerUser(AddUserDto user, boolean dryRun);
}
