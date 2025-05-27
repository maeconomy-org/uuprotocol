package io.recheck.uuidprotocol.common.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.cert.X509Certificate;
import java.util.Collection;

@Data
public class X509UserDetails implements UserDetails {

    @JsonIgnore
    private final X509Certificate certificate;

    private final String certFingerprint;

    private final String certCommonName;

    private final Collection<? extends GrantedAuthority> authorities;

    @JsonIgnore
    @Override
    public String getPassword() {
        return null; // Not used in certificate-based auth
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return null;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }
}
