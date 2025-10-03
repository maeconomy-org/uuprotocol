package io.recheck.uuidprotocol.nodenetwork.registrar;

import io.recheck.uuidprotocol.common.resttemplate.RestTemplateImpl;
import io.recheck.uuidprotocol.common.resttemplate.config.RestTemplateImplBuilder;
import io.recheck.uuidprotocol.common.resttemplate.model.ServerSpec;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UUIDRegistrarServiceConfig {

    @Bean
    public RestTemplateImpl restTemplateImplUUIDRegistrar(SslBundles sslBundles, ServerSpec serverSpecUUIDRegistrar) {
        return RestTemplateImplBuilder.build(sslBundles, serverSpecUUIDRegistrar);
    }

    @Bean
    @ConfigurationProperties(prefix = "uuid-service-registrar-config.server-spec")
    public ServerSpec serverSpecUUIDRegistrar() {
        return new ServerSpec();
    }

}
