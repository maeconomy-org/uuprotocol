package io.recheck.uuidprotocol.nodenetwork.owner;

import io.recheck.uuidprotocol.common.resttemplate.RestTemplateImpl;
import io.recheck.uuidprotocol.common.resttemplate.config.RestTemplateImplBuilder;
import io.recheck.uuidprotocol.common.resttemplate.model.ServerSpec;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UUIDOwnerServiceConfig {

    @Bean
    public RestTemplateImpl restTemplateImpl(SslBundles sslBundles, ServerSpec serverSpec) {
        return RestTemplateImplBuilder.build(sslBundles, serverSpec);
    }

    @Bean
    @ConfigurationProperties(prefix = "uuid-owner-service-config.server-spec")
    public ServerSpec serverSpec() {
        return new ServerSpec();
    }

}
