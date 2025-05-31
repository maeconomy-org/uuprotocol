package io.recheck.uuidprotocol.common.resttemplate.config;

import io.recheck.uuidprotocol.common.resttemplate.RestTemplateImpl;
import io.recheck.uuidprotocol.common.resttemplate.config.logging.LoggingInterceptor;
import io.recheck.uuidprotocol.common.resttemplate.model.SSLContextSpec;
import io.recheck.uuidprotocol.common.resttemplate.model.ServerSpec;
import lombok.SneakyThrows;
import org.apache.hc.client5.http.config.TlsConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.DefaultClientTlsStrategy;
import org.apache.hc.client5.http.ssl.TlsSocketStrategy;
import org.apache.hc.core5.http.ssl.TLS;
import org.apache.hc.core5.ssl.PrivateKeyStrategy;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.apache.hc.core5.util.Timeout;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.security.cert.X509Certificate;
import java.util.Collections;

public class RestTemplateImplBuilder {

    @SneakyThrows
    public static RestTemplateImpl build(SSLContextSpec sslContextSpec, ServerSpec serverSpec) {

        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

        PrivateKeyStrategy privateKeyStrategy = (aliases, sslParameters) -> sslContextSpec.getKeyStoreAlias();
        File keyStoreFile = ResourceUtils.getFile(sslContextSpec.getKeyStoreFile());

        SSLContext sslContext = SSLContexts.custom()
                .loadTrustMaterial(null, acceptingTrustStrategy)
                .loadKeyMaterial(keyStoreFile,
                        sslContextSpec.getKeystorePassword().toCharArray(),
                        sslContextSpec.getKeystorePassword().toCharArray(),
                        privateKeyStrategy)
                .build();

        return build(sslContext, serverSpec);
    }

    private static RestTemplateImpl build(SSLContext sslContext, ServerSpec serverSpec) {
        final TlsSocketStrategy tlsStrategy = new DefaultClientTlsStrategy(sslContext);
        final HttpClientConnectionManager cm = PoolingHttpClientConnectionManagerBuilder.create()
                .setTlsSocketStrategy(tlsStrategy)
                .setDefaultTlsConfig(TlsConfig.custom()
                        .setHandshakeTimeout(Timeout.ofSeconds(30))
                        .setSupportedProtocols(TLS.V_1_2, TLS.V_1_3)
                        .build())
                .build();

        CloseableHttpClient httpclient = HttpClients.custom()
                .setConnectionManager(cm)
                .build();

        RestTemplate restTemplate = new RestTemplateBuilder()
                .additionalInterceptors(Collections.singletonList(new LoggingInterceptor()))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory(httpclient))
                .build();

        return new RestTemplateImpl(restTemplate, serverSpec);
    }

}
