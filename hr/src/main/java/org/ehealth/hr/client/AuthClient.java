package org.ehealth.hr.client;

import org.ehealth.hr.domain.dto.auth.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "gatekeeper", url = "${client.services.auth}/api/gatekeeper")
public interface AuthClient {

    @GetMapping("/v1/user/current")
    UserDto findCurrentUser();

    @PutMapping("/v1/user/cui/{cui}")
    void updateUserActive(@PathVariable("cui") String cui);
}
