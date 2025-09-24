package io.recheck.uuidprotocol.user.auth.x509.certificate;

import org.bouncycastle.asn1.x509.CertificatePolicies;
import org.bouncycastle.asn1.x509.PolicyInformation;

import java.util.HashSet;
import java.util.Set;

public class CertificatePoliciesDecoder {

    public static Set<String> decodeCertificatePolicies(byte[] extensionValue) {
        Set<String> policyIds = new HashSet<>();
        try {
            CertificatePolicies policies = CertificatePolicies.getInstance(extensionValue);
            PolicyInformation[] policyInformation = policies.getPolicyInformation();

            for (PolicyInformation policy : policyInformation) {
                policyIds.add(policy.getPolicyIdentifier().getId());
            }
        } catch (Exception e) {
            // Handle exception
        }
        return policyIds;
    }
}
