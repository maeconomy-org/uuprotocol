package io.recheck.uuidprotocol.service.registrar;

import io.recheck.uuidprotocol.common.yaml.YamlPropertySourceFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication(scanBasePackages = "io.recheck.uuidprotocol")
@PropertySource(value = {"application-uuid-registrar.yaml"}, factory = YamlPropertySourceFactory.class)
public class RegistrarApplication {

    public static void main(String[] args) {
        SpringApplication.run(RegistrarApplication.class, args);
    }


}
