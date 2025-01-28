package io.recheck.uuidprotocol.domain.uuidowner;

import io.recheck.uuidprotocol.common.datasource.AbstractFirestoreDataSource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OwnerUUIDDataSource extends AbstractFirestoreDataSource<OwnerUUID> {

    public OwnerUUIDDataSource() {
        super(OwnerUUID.class);
    }

    public List<OwnerUUID> findByOwnerCertFingerprint(String ownerCertFingerprint) {
        return whereEqualTo("ownerCertFingerprint", ownerCertFingerprint);
    }

    public List<OwnerUUIDDTO> findByOwnerCertFingerprintCastUUIDDTO(String ownerCertFingerprint) {
        return whereEqualTo("ownerCertFingerprint", ownerCertFingerprint, OwnerUUIDDTO.class);
    }

    public OwnerUUID findByUUID(String uuid) {
        List<OwnerUUID> ownerUUIDList = whereEqualTo("uuid", uuid);
        if (ownerUUIDList.isEmpty()) {
            return null;
        }
        return ownerUUIDList.get(0);
    }
}
