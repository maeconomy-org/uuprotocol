package io.recheck.uuidprotocol.nodenetwork.owner;

import io.recheck.uuidprotocol.common.resttemplate.RestTemplateImpl;
import io.recheck.uuidprotocol.common.resttemplate.config.RestTemplateImplBuilder;
import io.recheck.uuidprotocol.common.resttemplate.model.SSLContextSpec;
import io.recheck.uuidprotocol.common.resttemplate.model.ServerSpec;
import lombok.SneakyThrows;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import javax.net.ssl.SSLContext;
import java.security.cert.X509Certificate;

@Configuration
public class UUIDOwnerServiceConfig {

    private RestTemplateImplBuilder restTemplateImplBuilder = new RestTemplateImplBuilder();

    @Bean
    @SneakyThrows
    public RestTemplateImpl restTemplateImpl(SSLContextSpec sslContextSpec, ServerSpec serverSpec) {
        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
        SSLContext sslContext = SSLContexts.custom()
                .loadTrustMaterial(null, acceptingTrustStrategy)
                .loadKeyMaterial(ResourceUtils.getFile(sslContextSpec.getKeyStoreFile()), sslContextSpec.getKeystorePassword().toCharArray(), sslContextSpec.getKeystorePassword().toCharArray(), (map, sslParameters) -> sslContextSpec.getKeyStoreAlias())
                .build();
        return restTemplateImplBuilder.build(sslContext, serverSpec);
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
