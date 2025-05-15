package org.ehealth.gatekeeper.service;

import static org.assertj.core.api.BDDAssertions.catchThrowableOfType;
import static org.assertj.core.api.BDDAssertions.then;
import static org.ehealth.gatekeeper.util.ThenMockAlias.thenMock;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.Optional;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private EmployeeClient employeeClient;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private UserService userService;

    @Test
    void canFindUserByEmail() {
        // given
        String email = "whithout@email.com";
        UserDto expectedUser = UserDto.builder()
                .email(email)
                .active(true)
                .build();

        given(userRepository.findByEmail(email, UserDto.class))
                .willReturn(Optional.of(expectedUser.toBuilder().build()));

        // when
        Optional<UserDto> actualUser = userService.findUserByEmail(email);

        // then
        then(actualUser).get()
                .usingRecursiveComparison()
                .isEqualTo(expectedUser);
    }

    @Test
    void cantRegisterUser_WhenDryRunIsTrue() {
        // given
        long roleId = 1L;
        String email = "add@user.com";
        String password = "this is a password";
        String cui = "123456789";
        AddUserDto user = AddUserDto.builder()
                .email(email)
                .password(password)
                .cui(cui)
                .build();
        RoleEntity role = RoleEntity.builder()
                .id(roleId)
                .name("USER")
                .build();

        given(userRepository.existsByEmail(email)).willReturn(false);
        given(userRepository.existsByCui(cui)).willReturn(false);
        given(roleRepository.findById(roleId)).willReturn(Optional.of(role.toBuilder().build()));

        // when
        userService.registerUser(user, true);

        // then
        thenMock(userRepository).should().existsByEmail(email);
        thenMock(userRepository).should().existsByCui(cui);
        thenMock(roleRepository).should().findById(roleId);
        thenMock(userRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void canRegisterUser_WhenDryRunIsFalse() {
        // given
        long roleId = 1L;
        String email = "add@user.com";
        String encripted = "encrypted password";
        String password = "this is a password";
        String cui = "123456789";
        AddUserDto user = AddUserDto.builder()
                .email(email)
                .password(password)
                .cui(cui)
                .build();
        RoleEntity role = RoleEntity
                .builder()
                .id(roleId)
                .name("USER")
                .build();
        UserEntity expectedUser = UserEntity.builder()
                .email(email)
                .password(encripted)
                .cui(cui)
                .role(role)
                .build();

        given(userRepository.existsByEmail(email)).willReturn(false);
        given(userRepository.existsByCui(cui)).willReturn(false);
        given(roleRepository.findById(roleId)).willReturn(Optional.of(role.toBuilder().build()));
        given(encoder.encode(password)).willReturn(encripted);

        // when
        userService.registerUser(user, false);

        // then
        thenMock(userRepository).should().save(refEq(expectedUser));
    }

    @Test
    void cantRegisterUser_WhenUserEmailAlreadyExists() {
        // given
        String email = "add@user.com";
        String password = "this is a password";
        String cui = "123456789";
        AddUserDto user = AddUserDto.builder()
                .email(email)
                .password(password)
                .cui(cui)
                .build();

        given(userRepository.existsByEmail(email)).willReturn(true);

        // when
        catchThrowableOfType(RequestConflictException.class, () -> userService.registerUser(user, false));

        // then
        thenMock(userRepository).should().existsByEmail(email);
        thenMock(userRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void cantRegisterUser_WhenUserCuiAlreadyExists() {
        // given
        String email = "add@user.com";
        String password = "this is a password";
        String cui = "123456789";
        AddUserDto user = AddUserDto.builder()
                .email(email)
                .password(password)
                .cui(cui)
                .build();

        given(userRepository.existsByEmail(email)).willReturn(false);
        given(userRepository.existsByCui(cui)).willReturn(true);

        // when
        catchThrowableOfType(RequestConflictException.class, () -> userService.registerUser(user, false));

        // then
        thenMock(userRepository).should().existsByEmail(email);
        thenMock(userRepository).should().existsByCui(cui);
        thenMock(userRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void cantRegisterUser_WhenRoleDoesntExist() {
        // given
        long roleId = 1L;
        String email = "add@user.com";
        String password = "this is a password";
        String cui = "123456789";
        AddUserDto user = AddUserDto.builder()
                .email(email)
                .password(password)
                .cui(cui)
                .build();

        given(userRepository.existsByEmail(email)).willReturn(false);
        given(userRepository.existsByCui(cui)).willReturn(false);
        given(roleRepository.findById(roleId)).willReturn(Optional.empty());

        // when
        catchThrowableOfType(ValueNotFoundException.class, () -> userService.registerUser(user, false));

        // then
        thenMock(userRepository).should().existsByEmail(email);
        thenMock(userRepository).should().existsByCui(cui);
        thenMock(roleRepository).should().findById(roleId);
        thenMock(userRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void canGetEmployeeUsers() {
        // given
        List<UserEmployeeDto> expectedUsers = List.of();
        List<EmployeeDto> employees = List.of();
        List<UserDto> users = List.of();

        given(employeeClient.findAllEmployees()).willReturn(employees);

        // when
        List<UserEmployeeDto> actualUsers = userService.getEmployeeUsers(users);

        // then
        then(actualUsers).isEqualTo(expectedUsers);
    }

    @Test
    void canUpdateUserActive() {
        // given
        long userId = 1L;
        boolean active = true;
        UserEntity user = UserEntity.builder()
                .id(userId)
                .email("test@test.com")
                .password("password")
                .cui("123456789")
                .role(RoleEntity.builder().id(1L).name("USER").build())
                .build();
        UserEntity expectedUser = user.toBuilder().active(active).build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user.toBuilder().build()));

        // when
        userService.updateUserActive(userId, active);

        // then
        thenMock(userRepository).should().save(refEq(expectedUser));
    }

    @Test
    void canUpdateRole() {
        // given
        long userId = 1L;
        long roleId = 2L;
        UserEntity user = UserEntity.builder()
                .id(userId)
                .email("test@test.com")
                .password("password")
                .cui("123456789")
                .role(RoleEntity.builder().id(3L).name("to change").build())
                .build();
        RoleEntity role = RoleEntity.builder().id(roleId).name("new role").build();
        UserEntity expectedUser = user.toBuilder().role(role).build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user.toBuilder().build()));
        given(roleRepository.findById(roleId)).willReturn(Optional.of(role.toBuilder().build()));

        // when
        userService.updateRole(userId, roleId);

        // then
        thenMock(userRepository).should().save(refEq(expectedUser));
    }
}
