package io.recheck.uuidprotocol.nodenetwork.owner;

import io.recheck.uuidprotocol.common.resttemplate.RestTemplateImpl;
import io.recheck.uuidprotocol.common.resttemplate.config.RestTemplateImplConfig;
import io.recheck.uuidprotocol.common.resttemplate.model.SSLContextSpec;
import io.recheck.uuidprotocol.common.resttemplate.model.ServerSpec;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UUIDOwnerServiceConfig {

    private RestTemplateImplConfig restTemplateImplConfig = new RestTemplateImplConfig();

    @Bean
    public RestTemplateImpl restTemplateImpl(SSLContextSpec sslContextSpec, ServerSpec serverSpec) {
        return restTemplateImplConfig.buildSSLRestTemplate(sslContextSpec, serverSpec);
    }

    @Bean
    @ConfigurationProperties(prefix = "ssl-context-spec")
    public SSLContextSpec sslContextSpec() {
        return new SSLContextSpec();
    }

    @Bean
    @ConfigurationProperties(prefix = "server-spec")
    public ServerSpec serverSpec() {
        return new ServerSpec();
    }

}
