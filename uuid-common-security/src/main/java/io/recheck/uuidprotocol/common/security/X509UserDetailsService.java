package io.recheck.uuidprotocol.common.security;

import io.recheck.uuidprotocol.common.security.certificate.CertificateInfo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.security.cert.X509Certificate;
import java.util.List;

@Slf4j
public class X509UserDetailsService implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {

    @Override
    @SneakyThrows
    public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken token) throws UsernameNotFoundException {
        X509Certificate certificate = (X509Certificate)token.getCredentials();

        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_CLIENT"));

        CertificateInfo certificateInfo = new CertificateInfo(certificate);
        log.debug("{}", certificateInfo);

        return new X509UserDetails(certificateInfo, authorities);
    }
}
