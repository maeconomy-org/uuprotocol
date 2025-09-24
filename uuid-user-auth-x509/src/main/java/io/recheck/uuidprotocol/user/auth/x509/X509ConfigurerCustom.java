package io.recheck.uuidprotocol.user.auth.x509;

import io.recheck.uuidprotocol.common.yaml.YamlPropertySourceFactory;
import io.recheck.uuidprotocol.user.auth.x509.filter.X509AuthenticationFilterCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@PropertySource(value = {"application-user-auth-x509-${spring.profiles.active}.yaml"}, factory = YamlPropertySourceFactory.class)
public class X509ConfigurerCustom extends AbstractHttpConfigurer<X509ConfigurerCustom, HttpSecurity> {

    private final X509UserDetailsService x509UserDetailsService;

    @Override
    public void init(HttpSecurity http) {
        PreAuthenticatedAuthenticationProvider authenticationProvider = new PreAuthenticatedAuthenticationProvider();
        authenticationProvider.setPreAuthenticatedUserDetailsService(x509UserDetailsService);
        http.authenticationProvider(authenticationProvider)
                .setSharedObject(AuthenticationEntryPoint.class, new Http403ForbiddenEntryPoint());
    }

    @Override
    public void configure(HttpSecurity http) {
        X509AuthenticationFilterCustom x509AuthenticationFilter = new X509AuthenticationFilterCustom();
        x509AuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        x509AuthenticationFilter.setSecurityContextRepository(new RequestAttributeSecurityContextRepository());
        x509AuthenticationFilter.setSecurityContextHolderStrategy(getSecurityContextHolderStrategy());
        x509AuthenticationFilter = postProcess(x509AuthenticationFilter);

        http.addFilterBefore(x509AuthenticationFilter, AbstractPreAuthenticatedProcessingFilter.class);
    }


}
