package io.recheck.uuidprotocol.common.security;

import io.recheck.uuidprotocol.common.security.filter.X509ConfigurerCustom;
import io.recheck.uuidprotocol.common.yaml.YamlPropertySourceFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@PropertySource(value = {"application-common-security-${spring.profiles.active}.yaml"}, factory = YamlPropertySourceFactory.class)
public class X509ClientsSecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authorizeHttpRequests) ->
                        authorizeHttpRequests.anyRequest().authenticated()
                )
                .with(new X509ConfigurerCustom<>(), httpSecurityX509ConfigurerCustom -> {})
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.NEVER))
                .csrf(AbstractHttpConfigurer::disable);

        SecurityFilterChain chain = http.build();
        log.info("Configured security filter chain: {}",chain);
        return chain;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("*");
        configuration.setAllowedMethods(Arrays.asList("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization", "Content-Type", "Accept", "Origin", "CreatedBy",
                "Access-Control-Request-Method", "Access-Control-Request-Headers"
                ));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    @Bean
    public UserDetailsService getUserDetailsService() {
        return username -> {
            throw new AuthenticationServiceException("UserDetailsService is disabled");
        };
    }



}