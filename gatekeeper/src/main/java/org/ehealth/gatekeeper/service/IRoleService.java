package org.ehealth.gatekeeper.service;

import org.ehealth.gatekeeper.domain.dto.RoleDto;

import java.util.List;

public interface IRoleService {
    List<RoleDto> findAll();
}
