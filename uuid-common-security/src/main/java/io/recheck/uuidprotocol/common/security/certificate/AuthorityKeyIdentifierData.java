package io.recheck.uuidprotocol.common.security.certificate;

import java.util.List;

public class AuthorityKeyIdentifierData {

    private final String keyIdHex;
    private final List<String> authorityCertIssuer;
    private final String authorityCertSerialNumber;

    public AuthorityKeyIdentifierData(String keyIdHex, List<String> authorityCertIssuer, String authorityCertSerialNumber) {
        this.keyIdHex = keyIdHex;
        this.authorityCertIssuer = authorityCertIssuer;
        this.authorityCertSerialNumber = authorityCertSerialNumber;
    }

    public String getKeyIdHex() { return keyIdHex; }
    public List<String> getAuthorityCertIssuer() { return authorityCertIssuer; }
    public String getAuthorityCertSerialNumber() { return authorityCertSerialNumber; }

    @Override
    public String toString() {
        return "KeyId=" + keyIdHex +
                (authorityCertIssuer.isEmpty() ? "" : ", Issuer=" + authorityCertIssuer) +
                (authorityCertSerialNumber == null ? "" : ", Serial=" + authorityCertSerialNumber);
    }

}
