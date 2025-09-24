package io.recheck.uuidprotocol.user.auth.x509;

import io.recheck.uuidprotocol.user.domain.X509UserDetails;
import io.recheck.uuidprotocol.user.persistence.X509UserDataSource;
import io.recheck.uuidprotocol.user.service.X509UserService;
import io.recheck.uuidprotocol.user.auth.x509.certificate.CertificateInfo;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.cert.X509Certificate;

@Slf4j
@Service
@RequiredArgsConstructor
public class X509UserDetailsService implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {

    private final X509UserService x509UserService;
    private final X509UserDataSource x509UserDataSource;

    @Override
    @SneakyThrows
    public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken token) throws UsernameNotFoundException {
        X509Certificate certificate = (X509Certificate)token.getCredentials();

        CertificateInfo certificateInfo = new CertificateInfo(certificate);
        log.debug("{}", certificateInfo);

        X509UserDetails user = x509UserDataSource.findByCredentials(certificateInfo.getCertificateSha256());
        if (user == null) {
            user = x509UserService.create(new X509UserDetails(certificateInfo));
        }

        return user;
    }
}
