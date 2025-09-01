package io.recheck.uuidprotocol.common.security.certificate;

import lombok.Getter;
import lombok.SneakyThrows;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.text.SimpleDateFormat;
import java.util.*;

public class CertificateInfo {

    // Existing fields
    private final X509Certificate certificate;

    @Getter
    private final String certificateSha256;
    private final String certificateSha1;

    private final String publicKeyAlgorithm;
    private final String publicKeyFormat;
    private final int publicKeySizeBits;
    private final String publicKeyDetails;

    private final String publicKeySha256;
    private final String publicKeySha1;

    private final Map<String, String> subjectFields;

    @Getter
    private final String subjectCommonName;

    private final Map<String, String> issuerFields;
    private final String serialNumber;
    private final Date validFrom;
    private final Date validTo;
    private final String signatureAlgorithm;
    private final int version;

    private final List<String> subjectAlternativeNames;
    private final List<String> keyUsage;
    private final List<String> extendedKeyUsage;
    private final boolean isCA;
    private final Map<String, String> criticalExtensions;
    private final Map<String, String> nonCriticalExtensions;
    private final Map<String, byte[]> rawExtensions;

    private static final Map<String, String> EKU_NAMES = Map.ofEntries(
            Map.entry("1.3.6.1.5.5.7.3.1", "TLS Web Server Authentication"),
            Map.entry("1.3.6.1.5.5.7.3.2", "TLS Web Client Authentication"),
            Map.entry("1.3.6.1.5.5.7.3.3", "Code Signing"),
            Map.entry("1.3.6.1.5.5.7.3.4", "Email Protection"),
            Map.entry("1.3.6.1.5.5.7.3.8", "Time Stamping"),
            Map.entry("1.3.6.1.5.5.7.3.9", "OCSP Signing")
    );

    private static final Map<String, String> EXT_NAMES = Map.ofEntries(
            Map.entry("2.5.29.14", "Subject Key Identifier"),
            Map.entry("2.5.29.15", "Key Usage"),
            Map.entry("2.5.29.17", "Subject Alternative Name"),
            Map.entry("2.5.29.19", "Basic Constraints"),
            Map.entry("2.5.29.35", "Authority Key Identifier"),
            Map.entry("2.5.29.37", "Extended Key Usage"),
            Map.entry("2.5.29.31", "CRL Distribution Points"),
            Map.entry("2.5.29.32", "Certificate Policies")
    );

    @SneakyThrows
    public CertificateInfo(X509Certificate cert) {
        if (cert == null) throw new IllegalArgumentException("Certificate cannot be null");

        this.certificate = cert;

        this.subjectFields = parseDn(cert.getSubjectX500Principal().getName());
        this.subjectCommonName = subjectFields.get("CN");
        this.issuerFields  = parseDn(cert.getIssuerX500Principal().getName());
        this.serialNumber  = cert.getSerialNumber().toString(16);
        this.validFrom     = cert.getNotBefore();
        this.validTo       = cert.getNotAfter();
        this.signatureAlgorithm = cert.getSigAlgName();
        this.version       = cert.getVersion();
        this.isCA          = cert.getBasicConstraints() >= 0;

        this.subjectAlternativeNames = extractSANs(cert);
        this.keyUsage = extractKeyUsage(cert);
        this.extendedKeyUsage = extractExtendedKeyUsage(cert);

        this.criticalExtensions    = mapExtensionNames(cert.getCriticalExtensionOIDs());
        this.nonCriticalExtensions = mapExtensionNames(cert.getNonCriticalExtensionOIDs());
        this.rawExtensions         = extractRawExtensions(cert);

        PublicKey pk = certificate.getPublicKey();
        this.publicKeyAlgorithm = pk.getAlgorithm();
        this.publicKeyFormat = pk.getFormat();

        int sizeBits = -1;
        String details = "";

        if (pk instanceof RSAPublicKey rsa) {
            sizeBits = rsa.getModulus().bitLength();
            details = "RSA Public Exponent=" + rsa.getPublicExponent();
        } else if (pk instanceof ECPublicKey ec) {
            sizeBits = ec.getParams().getCurve().getField().getFieldSize();
            details = "EC Curve Field Size=" + sizeBits + " bits";
        } else {
            details = "Encoded length=" + pk.getEncoded().length + " bytes";
        }

        this.publicKeySizeBits = sizeBits;
        this.publicKeyDetails = details;

        // Compute fingerprints
        this.publicKeySha256 = computeFingerprint(pk.getEncoded(), "SHA-256").toLowerCase();
        this.publicKeySha1 = computeFingerprint(pk.getEncoded(), "SHA-1").toLowerCase();
        this.certificateSha256 = computeFingerprint(certificate.getEncoded(), "SHA-256").toLowerCase();
        this.certificateSha1 = computeFingerprint(certificate.getEncoded(), "SHA-1").toLowerCase();
    }

    private Map<String, String> parseDn(String dn) {
        Map<String, String> fields = new LinkedHashMap<>();
        String[] parts = dn.split(",\\s*");
        for (String part : parts) {
            String[] kv = part.split("=", 2);
            if (kv.length == 2) fields.put(kv[0], kv[1]);
        }
        return fields;
    }

    private List<String> extractSANs(X509Certificate cert) {
        List<String> list = new ArrayList<>();
        try {
            Collection<List<?>> sans = cert.getSubjectAlternativeNames();
            if (sans != null) {
                for (List<?> sanItem : sans) {
                    Integer type = (Integer) sanItem.get(0);
                    Object value = sanItem.get(1);
                    list.add(sanTypeName(type) + ": " + value);
                }
            }
        } catch (CertificateParsingException ignored) {}
        return list;
    }

    private String sanTypeName(int type) {
        return switch (type) {
            case 0 -> "OtherName";
            case 1 -> "RFC822Name (email)";
            case 2 -> "DNSName";
            case 3 -> "X400Address";
            case 4 -> "DirectoryName";
            case 5 -> "EDI Party Name";
            case 6 -> "URI";
            case 7 -> "IPAddress";
            case 8 -> "RegisteredID";
            default -> "Unknown";
        };
    }

    private List<String> extractKeyUsage(X509Certificate cert) {
        boolean[] keyUsage = cert.getKeyUsage();
        if (keyUsage == null) return Collections.emptyList();
        String[] kuNames = {
                "digitalSignature", "nonRepudiation", "keyEncipherment", "dataEncipherment",
                "keyAgreement", "keyCertSign", "cRLSign", "encipherOnly", "decipherOnly"
        };
        List<String> usages = new ArrayList<>();
        for (int i = 0; i < keyUsage.length; i++) {
            if (keyUsage[i]) usages.add(kuNames[i]);
        }
        return usages;
    }

    private List<String> extractExtendedKeyUsage(X509Certificate cert) {
        try {
            List<String> eku = cert.getExtendedKeyUsage();
            if (eku == null) return Collections.emptyList();
            List<String> mapped = new ArrayList<>();
            for (String oid : eku) {
                mapped.add(EKU_NAMES.getOrDefault(oid, oid));
            }
            return mapped;
        } catch (CertificateParsingException e) {
            return Collections.emptyList();
        }
    }

    private Map<String, String> mapExtensionNames(Set<String> oids) {
        Map<String, String> mapped = new LinkedHashMap<>();
        if (oids != null) {
            for (String oid : oids) {
                mapped.put(oid, EXT_NAMES.getOrDefault(oid, "Unknown Extension"));
            }
        }
        return mapped;
    }

    private Map<String, byte[]> extractRawExtensions(X509Certificate cert) {
        Map<String, byte[]> map = new LinkedHashMap<>();
        Set<String> allOids = new LinkedHashSet<>();
        if (cert.getCriticalExtensionOIDs() != null) allOids.addAll(cert.getCriticalExtensionOIDs());
        if (cert.getNonCriticalExtensionOIDs() != null) allOids.addAll(cert.getNonCriticalExtensionOIDs());

        for (String oid : allOids) {
            byte[] value = cert.getExtensionValue(oid);
            if (value != null) map.put(oid, value.clone());
        }
        return map;
    }

    // ===== Extension Decoders =====

    @SneakyThrows
    private String decodeSubjectKeyIdentifier() {
        byte[] skiExtensionValue = certificate.getExtensionValue(Extension.subjectKeyIdentifier.getId());
        if (skiExtensionValue == null) return null;

        ASN1InputStream ais = new ASN1InputStream(skiExtensionValue);
        ASN1Primitive primitive = ais.readObject();
        byte[] skiBytes = ((ASN1OctetString) primitive).getOctets();
        return bytesToHex(skiBytes);
    }

    private AuthorityKeyIdentifierData decodeAuthorityKeyIdentifierFull() {
        try {
            // 1) Pull the raw extension bytes (OID 2.5.29.35)
            byte[] ext = certificate.getExtensionValue(Extension.authorityKeyIdentifier.getId());
            if (ext == null) return null;

            // 2) The value is an OCTET STRING that wraps the actual AKI DER
            byte[] inner = ASN1OctetString.getInstance(ext).getOctets();

            // 3) Parse the inner bytes as AuthorityKeyIdentifier
            AuthorityKeyIdentifier aki =
                    AuthorityKeyIdentifier.getInstance(ASN1Primitive.fromByteArray(inner));

            // 4) Extract fields
            String keyIdHex = null;
            byte[] keyId = aki.getKeyIdentifier();
            if (keyId != null) keyIdHex = bytesToHex(keyId);

            List<String> issuers = new ArrayList<>();
            GeneralNames names = aki.getAuthorityCertIssuer();
            if (names != null) {
                for (GeneralName gn : names.getNames()) {
                    switch (gn.getTagNo()) {
                        case GeneralName.directoryName -> {
                            X500Name x500 = X500Name.getInstance(gn.getName());
                            // RFC 4519 style string (e.g., "CN=Root CA,O=Example")
                            issuers.add(x500.toString());
                        }
                        case GeneralName.uniformResourceIdentifier, GeneralName.dNSName, GeneralName.rfc822Name, GeneralName.iPAddress -> {
                            issuers.add(gn.getName().toString()); // URI
                        }
                        default -> issuers.add("GeneralName(" + gn.getTagNo() + "): " + gn.getName());
                    }
                }
            }

            String serialHex = null;
            BigInteger serial = aki.getAuthorityCertSerialNumber();
            if (serial != null) serialHex = serial.toString(16).toUpperCase();

            return new AuthorityKeyIdentifierData(keyIdHex, issuers, serialHex);
        } catch (Exception e) {
            return null; // robust in face of odd/partial AKI encodings
        }
    }

    private List<String> decodeCRLDistributionPoints() {
        byte[] der = rawExtensions.get("2.5.29.31");

        if (der == null) {
            return Collections.emptyList();
        }

        return CRLDistributionPointsDecoder.decodeCRLDistributionPoints(der);
    }

    private Set<String> decodeCertificatePolicies() {
        byte[] der = rawExtensions.get("2.5.29.32");
        if (der == null) {
            return Collections.emptySet();
        }
        return CertificatePoliciesDecoder.decodeCertificatePolicies(der);
    }

    // ===== Helpers =====
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02X", b));
        return sb.toString();
    }

    @SneakyThrows
    private static String computeFingerprint(byte[] data, String algo) {
        MessageDigest md = MessageDigest.getInstance(algo);
        byte[] digest = md.digest(data);
        return bytesToHex(digest);
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return """
                === Certificate Info ===
                Subject: %s
                Subject Common Name: %s
                Issuer: %s
                Serial Number: %s
                Certificate SHA-256: %s
                Certificate SHA-1: %s
                Valid From: %s
                Valid To: %s
                Signature Algorithm: %s
                Public Key Algorithm: %s
                Public Key Format: %s
                Public Key Details: %s
                Public Key Size: %s
                Public Key SHA-256: %s
                Public Key SHA-1: %s
                Version: %d
                CA: %s
                SANs: %s
                Key Usage: %s
                Extended Key Usage: %s
                Critical Extensions: %s
                Non-Critical Extensions: %s
                Subject Key Identifier: %s
                Authority Key Identifier: %s
                CRL Distribution Points: %s
                Certificate Policies: %s
                """.formatted(
                subjectFields, subjectCommonName, issuerFields, serialNumber,
                certificateSha256, certificateSha1,
                sdf.format(validFrom), sdf.format(validTo),
                signatureAlgorithm, publicKeyAlgorithm,
                publicKeyFormat, publicKeyDetails, publicKeySizeBits,
                publicKeySha256, publicKeySha1,
                version, isCA,
                subjectAlternativeNames, keyUsage, extendedKeyUsage,
                criticalExtensions, nonCriticalExtensions,
                decodeSubjectKeyIdentifier(), decodeAuthorityKeyIdentifierFull(),
                decodeCRLDistributionPoints(), decodeCertificatePolicies()
        );
    }
}

