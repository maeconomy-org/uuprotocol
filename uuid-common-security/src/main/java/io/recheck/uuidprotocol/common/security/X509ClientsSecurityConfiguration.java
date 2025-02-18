package io.recheck.uuidprotocol.common.security;

import io.recheck.uuidprotocol.common.yaml.YamlPropertySourceFactory;
import jakarta.xml.bind.DatatypeConverter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.security.MessageDigest;
import java.security.cert.X509Certificate;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@PropertySource(value = {"application-common-security.yaml"}, factory = YamlPropertySourceFactory.class)
public class X509ClientsSecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authorizeHttpRequests) ->
                        authorizeHttpRequests.anyRequest().authenticated()
                )
                .x509(httpSecurityX509Configurer -> {
                    httpSecurityX509Configurer.authenticationUserDetailsService(new AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken>() {
                        @SneakyThrows
                        public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken token) throws UsernameNotFoundException {
                            X509Certificate cert = (X509Certificate)token.getCredentials();
                            MessageDigest md = MessageDigest.getInstance("SHA-256");
                            md.update(cert.getEncoded());
                            String fingerprint = DatatypeConverter.printHexBinary(md.digest()).toLowerCase();

                            return new User(fingerprint, "", AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_CLIENT"));
                        }
                    });
                })
                .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.NEVER))
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public UserDetailsService getUserDetailsService() {
        return username -> {
            throw new AuthenticationServiceException("UserDetailsService is disabled");
        };
    }



}