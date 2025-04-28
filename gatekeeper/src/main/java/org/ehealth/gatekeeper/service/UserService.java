package org.ehealth.gatekeeper.service;

import java.util.Optional;

import org.ehealth.gatekeeper.domain.dto.AddUserDto;
import org.ehealth.gatekeeper.domain.dto.UserDto;
import org.ehealth.gatekeeper.domain.entity.EmployeeEntity;
import org.ehealth.gatekeeper.domain.entity.RoleEntity;
import org.ehealth.gatekeeper.domain.entity.UserEntity;
import org.ehealth.gatekeeper.domain.exception.RequestConflictException;
import org.ehealth.gatekeeper.repository.EmployeeRepository;
import org.ehealth.gatekeeper.repository.RoleRepository;
import org.ehealth.gatekeeper.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder encoder;

    @Override
    public Optional<UserDto> findUserByEmail(String email) {
        return userRepository.findByEmail(email, UserDto.class);
    }

    @Override
    @Transactional
    public void registerUser(AddUserDto user, boolean dryRun) {
        if (userRepository.existsByEmail(user.email())) {
            throw new RequestConflictException("El email que se intenta registrar ya esta en uso");
        }
        if (employeeRepository.existsByCui(user.cui())) {
            throw new RequestConflictException("El CUI que se intenta registrar ya esta en uso");
        }
        RoleEntity role = roleRepository.findByName(user.role(), RoleEntity.class)
                .orElseThrow(() -> new RequestConflictException("El rol que se intenta asignar no existe"));

        if (dryRun) {
            return;
        }
        String encryptedPassword = encoder.encode(user.password());

        EmployeeEntity employee = EmployeeEntity.builder()
                .fullName(user.fullName())
                .cui(user.cui())
                .phone(user.phone())
                .build();

        UserEntity newUser = UserEntity.builder()
                .email(user.email())
                .password(encryptedPassword)
                .role(role)
                .employee(employee)
                .build();

        userRepository.save(newUser);
    }
}
