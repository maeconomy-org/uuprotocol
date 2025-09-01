package io.recheck.uuidprotocol.common.security.certificate;

import org.bouncycastle.asn1.x509.*;

import java.util.ArrayList;
import java.util.List;

public class CRLDistributionPointsDecoder {

    public static List<String> decodeCRLDistributionPoints(byte[] extensionValue) {
        List<String> crlUrls = new ArrayList<>();
        try {
            CRLDistPoint crlDistPoint = CRLDistPoint.getInstance(extensionValue);
            DistributionPoint[] distributionPoints = crlDistPoint.getDistributionPoints();

            for (DistributionPoint dp : distributionPoints) {
                DistributionPointName dpn = dp.getDistributionPoint();
                if (dpn != null && dpn.getType() == DistributionPointName.FULL_NAME) {
                    GeneralNames names = GeneralNames.getInstance(dpn.getName());
                    for (GeneralName gn : names.getNames()) {
                        if (gn.getTagNo() == GeneralName.uniformResourceIdentifier) {
                            crlUrls.add(gn.getName().toString());
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Handle exception
        }
        return crlUrls;
    }
}
