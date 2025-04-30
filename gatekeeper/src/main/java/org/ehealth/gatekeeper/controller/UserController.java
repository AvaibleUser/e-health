package org.ehealth.gatekeeper.controller;

import org.ehealth.gatekeeper.domain.dto.UserDto;
import org.ehealth.gatekeeper.util.annotation.CurrentUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor
public class UserController {

    @GetMapping("/current")
    public UserDto findCurrentUser(@CurrentUser UserDto user) {
        return user;
    }
}
