package org.ehealth.gatekeeper.service;

import java.util.Optional;

import org.ehealth.gatekeeper.client.EmployeeClient;
import org.ehealth.gatekeeper.domain.dto.AddUserDto;
import org.ehealth.gatekeeper.domain.dto.UserDto;
import org.ehealth.gatekeeper.domain.entity.RoleEntity;
import org.ehealth.gatekeeper.domain.entity.UserEntity;
import org.ehealth.gatekeeper.domain.exception.RequestConflictException;
import org.ehealth.gatekeeper.domain.exception.ValueNotFoundException;
import org.ehealth.gatekeeper.repository.RoleRepository;
import org.ehealth.gatekeeper.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import feign.FeignException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EmployeeClient employeeClient;
    private final PasswordEncoder encoder;

    @Override
    public Optional<UserDto> findUserByEmail(String email) {
        Optional<UserDto> user = userRepository.findByEmail(email, UserDto.class);
        if (user.isPresent() && user.map(UserDto::active).map(Boolean.TRUE::equals).get()) {
            throw new RequestConflictException("El usuario aun no ha sido activado por el administrador");
        }
        return user;
    }

    @Override
    @Transactional
    public void registerUser(AddUserDto user, boolean dryRun) {
        if (userRepository.existsByEmail(user.email())) {
            throw new RequestConflictException("El email que se intenta registrar ya esta en uso");
        }
        if (userRepository.existsByCui(user.cui())) {
            throw new RequestConflictException("El CUI que se intenta registrar ya esta en uso");
        }
        RoleEntity role = roleRepository.findById(1L)
                .orElseThrow(() -> new ValueNotFoundException("El rol provisional que se intenta asignar no existe"));
        try {
            employeeClient.findEmployeeByCui(user.cui());
        } catch (FeignException e) {
            throw new RequestConflictException("El CUI que se intenta registrar no existe");
        }

        if (dryRun) {
            return;
        }
        String encryptedPassword = encoder.encode(user.password());

        UserEntity newUser = UserEntity.builder()
                .email(user.email())
                .password(encryptedPassword)
                .cui(user.cui())
                .role(role)
                .build();

        userRepository.save(newUser);
    }
}
