package org.ehealth.gatekeeper.service;

import java.util.List;
import java.util.Optional;

import org.ehealth.gatekeeper.domain.dto.AddUserDto;
import org.ehealth.gatekeeper.domain.dto.UserDto;
import org.ehealth.gatekeeper.domain.dto.employee.UserEmployeeDto;

public interface IUserService {

    Optional<UserDto> findUserByEmail(String email);

    void registerUser(AddUserDto user, boolean dryRun);

    List<UserEmployeeDto> findAllUserByActiveTrue();

    List<UserEmployeeDto> findAllUserByActiveFalse();

    List<UserEmployeeDto> getEmployeeUsers(List<UserDto> users);

    void updateUserActive(Long userId, boolean active);

    void updateRole(Long userId, Long roleId);

    void updateUserActiveCui(String cui);
}
