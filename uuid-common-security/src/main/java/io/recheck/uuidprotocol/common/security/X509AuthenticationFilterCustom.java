package io.recheck.uuidprotocol.common.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.authentication.preauth.x509.SubjectDnX509PrincipalExtractor;
import org.springframework.security.web.authentication.preauth.x509.X509PrincipalExtractor;

import java.security.cert.X509Certificate;

public class X509AuthenticationFilterCustom extends AbstractPreAuthenticatedProcessingFilterCustom {

    private X509PrincipalExtractor principalExtractor = new SubjectDnX509PrincipalExtractor();

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        X509Certificate cert = extractClientCertificate(request);
        return (cert != null) ? this.principalExtractor.extractPrincipal(cert) : null;
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return extractClientCertificate(request);
    }

    private X509Certificate extractClientCertificate(HttpServletRequest request) {
        X509Certificate[] certs = (X509Certificate[]) request.getAttribute("jakarta.servlet.request.X509Certificate");
        if (certs != null && certs.length > 0) {
            return certs[0];
        }
        this.logger.debug("No client certificate found in request.");
        return null;
    }

    public void setPrincipalExtractor(X509PrincipalExtractor principalExtractor) {
        this.principalExtractor = principalExtractor;
    }

}
