package io.recheck.uuidprotocol.nodenetwork;

import io.recheck.uuidprotocol.common.yaml.YamlPropertySourceFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;



@SpringBootApplication(scanBasePackages = "io.recheck.uuidprotocol")
@PropertySource(value = {"application-node-network.yaml", "uuidowner-server-configuration.yaml"}, factory = YamlPropertySourceFactory.class)
//@PropertySource(value={"classpath:application-node-network-${spring.profiles.active}.properties"})
public class NodeNetworkApplication {

    public static void main(String[] args) {
        SpringApplication.run(NodeNetworkApplication.class, args);
    }

}
