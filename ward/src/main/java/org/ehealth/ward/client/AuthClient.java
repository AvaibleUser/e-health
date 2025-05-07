package org.ehealth.ward.client;

import org.ehealth.ward.domain.dto.client.auth.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "gatekeeper", url = "${client.services.auth}/api/gatekeeper")
public interface AuthClient {

    @GetMapping("/v1/user/current")
    UserDto findCurrentUser();
}
