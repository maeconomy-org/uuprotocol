package io.recheck.uuidprotocol.common.config.firebase;

import io.recheck.uuidprotocol.common.yaml.YamlPropertySourceFactory;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;

@Data
@Configuration
@PropertySource(value = "application-common.yaml", factory = YamlPropertySourceFactory.class)
@ConfigurationProperties(prefix = "gcp.firebase")
public class FirebaseProperties {

    private Resource serviceAccount;

}
