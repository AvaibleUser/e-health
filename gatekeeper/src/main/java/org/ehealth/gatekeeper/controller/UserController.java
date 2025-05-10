package org.ehealth.gatekeeper.controller;

import org.ehealth.gatekeeper.domain.dto.UserDto;
import org.ehealth.gatekeeper.domain.dto.employee.UserEmployeeDto;
import org.ehealth.gatekeeper.service.IUserService;
import org.ehealth.gatekeeper.util.annotation.CurrentUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @GetMapping("/current")
    public UserDto findCurrentUser(@CurrentUser UserDto user) {
        return user;
    }

    @GetMapping("/employees/active-true")
    public ResponseEntity<List<UserEmployeeDto>> findAllEmployeesWithActiveTrueUser() {
        List<UserEmployeeDto> result = userService.findAllUserByActiveTrue();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/employees/active-false")
    public ResponseEntity<List<UserEmployeeDto>> findAllEmployeesWithActiveFalseUser() {
        List<UserEmployeeDto> result = userService.findAllUserByActiveFalse();
        return ResponseEntity.ok(result);
    }

    @PutMapping("/accounts/{id}/active")
    public ResponseEntity<Void> updateUserActive(@PathVariable("id") Long userId, @RequestParam("active") boolean active) {

        userService.updateUserActive(userId, active);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/role/{id}")
    public ResponseEntity<Void> updateRole(@PathVariable("id") Long userId, @RequestParam("roleId") Long roleId) {

        userService.updateRole(userId, roleId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
