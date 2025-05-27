package io.recheck.uuidprotocol.common.security;

import jakarta.xml.bind.DatatypeConverter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.preauth.x509.SubjectDnX509PrincipalExtractor;

import java.security.MessageDigest;
import java.security.cert.X509Certificate;
import java.util.List;

@Slf4j
public class X509UserDetailsService implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {

    @Override
    @SneakyThrows
    public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken token) throws UsernameNotFoundException {
        X509Certificate certificate = (X509Certificate)token.getCredentials();
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(certificate.getEncoded());
        String certFingerprint = DatatypeConverter.printHexBinary(md.digest()).toLowerCase();

        SubjectDnX509PrincipalExtractor principalExtractor = new SubjectDnX509PrincipalExtractor();
        principalExtractor.setSubjectDnRegex("CN=(.*?)(?:,|$)");

        String certCommonName = principalExtractor.extractPrincipal(certificate).toString();

        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_CLIENT"));

        return new X509UserDetails(certificate, certFingerprint, certCommonName, authorities);
    }
}
