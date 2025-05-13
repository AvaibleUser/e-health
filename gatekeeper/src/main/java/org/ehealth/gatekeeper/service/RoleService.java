package org.ehealth.gatekeeper.service;

import lombok.RequiredArgsConstructor;
import org.ehealth.gatekeeper.domain.dto.RoleDto;
import org.ehealth.gatekeeper.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService implements IRoleService {
    private final RoleRepository roleRepository;

    @Override
    public List<RoleDto> findAll() {
        return this.roleRepository.findAllByOrderByCreatedAtDesc(RoleDto.class);
    }
}
