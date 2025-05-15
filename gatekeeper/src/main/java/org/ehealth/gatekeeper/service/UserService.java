package org.ehealth.gatekeeper.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.ehealth.gatekeeper.client.EmployeeClient;
import org.ehealth.gatekeeper.domain.dto.AddUserDto;
import org.ehealth.gatekeeper.domain.dto.UserDto;
import org.ehealth.gatekeeper.domain.dto.employee.EmployeeDto;
import org.ehealth.gatekeeper.domain.dto.employee.UserEmployeeDto;
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
        if (user.isPresent() && user.map(UserDto::active).map(Boolean.FALSE::equals).get()) {
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
            throw new RequestConflictException("El CUI que se intenta registrar no existe en pantilla de empelados");
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

    @Override
    public List<UserEmployeeDto> getEmployeeUsers(List<UserDto> users){
        List<EmployeeDto> employees;
        try {
            employees = this.employeeClient.findAllEmployees();
        } catch (FeignException e) {
            throw new RequestConflictException("No se ha podido obtener la lista de empleados, intente más tarde");
        }

        // Crear un mapa de usuarios por CUI para búsqueda rápida
        Map<String, UserDto> userByCui = users.stream()
                .collect(Collectors.toMap(UserDto::cui, Function.identity()));

        // Retornar solo los empleados que tengan usuario activo (coincidencia por cui)
        return employees.stream()
                .filter(emp -> userByCui.containsKey(emp.cui()))
                .map(emp -> {
                    UserDto user = userByCui.get(emp.cui());
                    return new UserEmployeeDto(user, emp);
                })
                .toList();
    }

    @Override
    public List<UserEmployeeDto> findAllUserByActiveTrue() {
        List<UserDto> users = this.userRepository.findAllByActiveTrueOrderByCreatedAtDesc(UserDto.class);
       return this.getEmployeeUsers(users);
    }

    @Override
    public List<UserEmployeeDto> findAllUserByActiveFalse() {
        List<UserDto> users = this.userRepository.findAllByActiveFalseOrderByCreatedAtDesc(UserDto.class);
        return this.getEmployeeUsers(users);
    }

    @Override
    public void updateUserActive(Long userId, boolean active) {
        UserEntity user = this.userRepository.findById(userId)
                .orElseThrow(() -> new ValueNotFoundException("El usuario no existe"));

        user.setActive(active);

        userRepository.save(user);

    }

    @Transactional
    @Override
    public void updateRole(Long userId, Long roleId) {
        UserEntity user = this.userRepository.findById(userId)
                .orElseThrow(() -> new ValueNotFoundException("El usuario no existe"));

        RoleEntity role = this.roleRepository.findById(roleId)
                .orElseThrow(() -> new ValueNotFoundException("El rol no existe"));

        user.setRole(role);

        userRepository.save(user);
    }



}
