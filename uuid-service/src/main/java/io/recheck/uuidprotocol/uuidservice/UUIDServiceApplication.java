package io.recheck.uuidprotocol.uuidservice;

import io.recheck.uuidprotocol.common.yaml.YamlPropertySourceFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication(scanBasePackages = "io.recheck.uuidprotocol")
@PropertySource(value = {"application-service.yaml"}, factory = YamlPropertySourceFactory.class)
public class UUIDServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UUIDServiceApplication.class, args);
    }


}
