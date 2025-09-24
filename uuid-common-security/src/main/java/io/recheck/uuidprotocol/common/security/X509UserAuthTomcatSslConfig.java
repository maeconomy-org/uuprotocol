package io.recheck.uuidprotocol.common.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class X509UserAuthTomcatSslConfig implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

    private final SslBundles sslBundles;

    @Value("${server-ssl-truststore-bundle}")
    private String bundleName;

    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        factory.addConnectorCustomizers(connector -> {
            SslBundle truststoreBundle = sslBundles.getBundle(bundleName);
            SSLHostConfig sslHostConfig = connector.findSslHostConfigs()[0];
            sslHostConfig.setCertificateVerification(Ssl.ClientAuth.map(Ssl.ClientAuth.NEED, "none", "optional", "required"));
            sslHostConfig.setTrustStore(truststoreBundle.getStores().getTrustStore());
        });
    }
}
