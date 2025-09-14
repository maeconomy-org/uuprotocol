package io.recheck.uuidprotocol.common.security.filter;

import io.recheck.uuidprotocol.common.security.X509UserDetailsService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;

public class X509ConfigurerCustom<H extends HttpSecurityBuilder<H>> extends AbstractHttpConfigurer<X509ConfigurerCustom<H>, H> {

    @Override
    public void init(H http) {
        PreAuthenticatedAuthenticationProvider authenticationProvider = new PreAuthenticatedAuthenticationProvider();
        authenticationProvider.setPreAuthenticatedUserDetailsService(new X509UserDetailsService());
        http.authenticationProvider(authenticationProvider)
                .setSharedObject(AuthenticationEntryPoint.class, new Http403ForbiddenEntryPoint());
    }

    @Override
    public void configure(H http) {
        X509AuthenticationFilterCustom x509AuthenticationFilter = new X509AuthenticationFilterCustom();
        x509AuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        x509AuthenticationFilter.setSecurityContextRepository(new RequestAttributeSecurityContextRepository());
        x509AuthenticationFilter.setSecurityContextHolderStrategy(getSecurityContextHolderStrategy());
        x509AuthenticationFilter = postProcess(x509AuthenticationFilter);

        http.addFilterBefore(x509AuthenticationFilter, AbstractPreAuthenticatedProcessingFilter.class);
    }


}
