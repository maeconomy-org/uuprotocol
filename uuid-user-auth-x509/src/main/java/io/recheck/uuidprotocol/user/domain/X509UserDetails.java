package io.recheck.uuidprotocol.user.domain;

import io.recheck.uuidprotocol.domain.user.UserDetailsCustom;
import io.recheck.uuidprotocol.user.auth.x509.certificate.CertificateInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class X509UserDetails extends UserDetailsCustom {

    private CertificateInfo certificateInfo;

    @Override
    public Object getCredentials() {
        return certificateInfo.getCertificateSha256();
    }
}
