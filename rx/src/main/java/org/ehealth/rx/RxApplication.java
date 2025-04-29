package org.ehealth.rx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class RxApplication {

	public static void main(String[] args) {
		SpringApplication.run(RxApplication.class, args);
	}

}
