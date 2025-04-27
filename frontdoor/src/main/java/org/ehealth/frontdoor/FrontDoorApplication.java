package org.ehealth.frontdoor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class FrontDoorApplication {

	public static void main(String[] args) {
		SpringApplication.run(FrontDoorApplication.class, args);
	}

}
