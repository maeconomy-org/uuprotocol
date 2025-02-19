package io.recheck.uuidprotocol.owner;

import io.recheck.uuidprotocol.common.yaml.YamlPropertySourceFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication(scanBasePackages = "io.recheck.uuidprotocol")
@PropertySource(value = {"application-owner.yaml"}, factory = YamlPropertySourceFactory.class)
public class UUIDOwnerApplication {

    public static void main(String[] args) {
        SpringApplication.run(UUIDOwnerApplication.class, args);
    }


}
